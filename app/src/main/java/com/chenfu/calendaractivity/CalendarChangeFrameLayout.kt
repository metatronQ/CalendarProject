package com.chenfu.calendaractivity

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast

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
     * 必返回true，即返回调用该container的消费方法则截断事件的传递
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
                } else if (currentY - startY > 100){
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

    fun start2Week() {
        // 手势向上，往上滑，切周
        Toast.makeText(context, "切换周", Toast.LENGTH_SHORT).show()
    }

    fun start2Month() {
        // 手势向下，往下滑，切月
        Toast.makeText(context, "切换月", Toast.LENGTH_SHORT).show()
    }
}