package com.chenfu.calendaractivity

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.GridView

class MyGridView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    GridView(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attributeSet,
        defStyleAttr,
        0
    )

    val TAG = "MyGridView"

    var startY = 0
    var startX = 0
    var currentY = 0
    var currentX = 0

    /**
     * 内部拦截法，配合CalendarChangeFrameLayout
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y.toInt()
                startX = ev.x.toInt()
                currentY = startY
                currentX = startX

                parent.requestDisallowInterceptTouchEvent(true)
                Log.d(TAG, "TouchListener: Down $currentY")
            }
            MotionEvent.ACTION_MOVE -> {
                currentY = ev.y.toInt()
                currentX = ev.x.toInt()
                Log.d(TAG, "TouchListener: Move $currentY")
                if ((startY - currentY) < 100 && (currentY - startY) < 100 && (startX - currentX) < 100 && (currentX - startX) < 100) {
                    // 半径100内，事件由当前拦截
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    // 水平大幅滑动和竖直大幅滑动，交给父布局，而由于父布局不处理水平滑动，最终抛给viewpager2
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "TouchListener: UP $currentY")
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     *
     * TODO：放弃
     * 两种设想：
     * 1.全部拦截，包括down，down的位置由此类记录，判断没有达到滑动要求则传递给item作为点击事件消费，动画由父布局实现
     * 2.全部不拦截，全部交给item，down的位置由item记录，相当于此类只起onInterceptTouchEvent return false的作用，
     *  之前gridview的touchlistener功能全部交给item去实现，不过item的父布局不是calendarlayout，这会比较麻烦
     *  */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//        return false
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }
}