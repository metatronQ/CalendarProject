package com.chenfu.calendaractivity.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.GridView
import android.widget.RelativeLayout
import com.chenfu.calendaractivity.GlobalField
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.adapter.HorizontalCalendarAdapter
import com.chenfu.calendaractivity.util.DisplayUtils

class CalendarChangeFrameLayout(
    context: Context,
    attributeSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : RelativeLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attributeSet,
        defStyleAttr,
        0
    )

    val TAG = "CalendarFrameLayout"
    var startY = 0
    var currentY = 0
    var isTrigger = false
    private lateinit var callback: HorizontalCalendarAdapter.Callback2Visibility

    // 默认动画为结束
    var isAnimationEnd = true
    var weekRaw = 1
    val dip50 = DisplayUtils.dip2px(context, 50f).toFloat()
    val dip300 = dip50 * 6
    var itemHeight = dip50

        override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        val action = ev!!.action
        if (action == MotionEvent.ACTION_DOWN) {
            startY = ev.y.toInt()
            currentY = startY
            isTrigger = false
            return false
        }
        return true
    }

    /**
     * 父布局处理竖直滑动事件
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y.toInt()
                currentY = startY
                isTrigger = false
                Log.d(TAG, "onTouchEvent: Down $startY")
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTrigger) return true
                currentY = event.y.toInt()
                Log.d(TAG, "onTouchEvent: Move $currentY")
                if ((startY - currentY) > 100) {
                    start2Week()
                    isTrigger = true
                } else if (currentY - startY > 100) {
                    start2Month()
                    isTrigger = true
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "onTouchEvent: Up $currentY")
                startY = 0
                currentY = 0
                isTrigger = false
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "onTouchEvent: Cancel")
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 只是更新位置1的视图（需要动画），位置0和2的视图需要在Adapter同步更新（不需要动画，只需要更新可见性）
     */
    fun start2Week() {
        // 手势向上，往上滑，切周
//        Toast.makeText(context, "切换周", Toast.LENGTH_SHORT).show()
        val monthGrid: GridView = findViewById(R.id.month_grid)
        val weekGrid: GridView = findViewById(R.id.week_grid)
        startAnimationForWeek(monthGrid, weekGrid)
    }

    fun startAnimationForWeek(monthGridView: GridView, weekGridView: GridView) {
        val animator: ValueAnimator = ValueAnimator.ofFloat(0f, -6 * dip50)
        animator.duration = 5000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            val displayTiming = -(weekRaw - 1) * dip50
            val translationY = it.animatedValue as Float
            Log.d(TAG, "startAnimationForWeek: $translationY")
            monthGridView.translationY = translationY
            if (isAbsoluteLess10(translationY, displayTiming)) {
                weekGridView.visibility = View.VISIBLE
            }

            if (dip300 + translationY >= itemHeight) {
                val lp = monthGridView.layoutParams
                lp.height = (dip300 + translationY).toInt()
                monthGridView.layoutParams = lp
            }
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                monthGridView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                GlobalField.isMonth = false
                monthGridView.visibility = View.GONE
                callback.updateVisibility()
            }
        })
        animator.start()
    }

    fun start2Month() {
        // 手势向下，往下滑，切月
//        Toast.makeText(context, "切换月", Toast.LENGTH_SHORT).show()
        val monthGrid: GridView = findViewById(R.id.month_grid)
        val weekGrid: GridView = findViewById(R.id.week_grid)
        startAnimationForMonth(monthGrid, weekGrid)
    }

    fun startAnimationForMonth(monthGridView: GridView, weekGridView: GridView) {
        val animator: ValueAnimator = ValueAnimator.ofFloat(-6 * dip50, 0f)
        animator.duration = 5000
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener {
            val displayTiming = -(weekRaw - 1) * dip50
            val translationY = it.animatedValue as Float
            Log.d(TAG, "startAnimationForWeek: $translationY")
            monthGridView.translationY = translationY
            if (isAbsoluteLess10(translationY, displayTiming)) {
                weekGridView.visibility = View.GONE
            }

            if (dip300 + translationY >= itemHeight) {
                val lp = monthGridView.layoutParams
                lp.height = (dip300 + translationY).toInt()
                monthGridView.layoutParams = lp
            }
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                // TODO：可能gridview重新显示的时候会重新bind item adapter，因此需要先设置true
                GlobalField.isMonth = true
                monthGridView.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                callback.updateVisibility()
            }
        })
        animator.start()
    }

    fun isAbsoluteLess10(translationY: Float, displayTiming: Float): Boolean {
        if (translationY == displayTiming) {
            return true
        }
        val absoluteA = Math.abs(translationY)
        val absoluteB = Math.abs(displayTiming)
        if (absoluteA > absoluteB) {
            if (absoluteA - absoluteB < 10) {
                return true
            }
        } else {
            if (absoluteB - absoluteA < 10) {
                return true
            }
        }
        return false
    }

    fun setWeekRowPosition(raw: Int, height: Float) {
        this.weekRaw = raw
        this.itemHeight = height
    }

    fun setCallback(callback2Visibility: HorizontalCalendarAdapter.Callback2Visibility) {
        this.callback = callback2Visibility
    }
}