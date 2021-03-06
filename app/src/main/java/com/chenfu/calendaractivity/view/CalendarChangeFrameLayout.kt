package com.chenfu.calendaractivity.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.chenfu.calendaractivity.Callback2Update

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
    private var callback: Callback2Update? = null

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
                    callback?.start2Week()
                    isTrigger = true
                } else if (currentY - startY > 100) {
                    callback?.start2Month()
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

    fun setCallback(callback2Update: Callback2Update?) {
        this.callback = callback2Update
    }
}