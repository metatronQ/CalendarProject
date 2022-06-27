package com.chenfu.calendaractivity

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2

/**
 * TODO:若要满足scrollview下滑至顶再下滑展开至月视图，在顶上滑先收缩至周视图，则需要先要在此类拦截全部的除down事件，即内部拦截法，然后在找到对应的gridview进行动画更新和点击事件处理
 * 若scrollview滑动与视图切换无关，则目前就可以满足，不拦截所有事件，当在gridview上滑动时会传给gridview，在其他区域滑动则由于没有其他能处理的view，最后交由本类处理
 */
class MyScrollView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    ScrollView(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attributeSet,
        defStyleAttr,
        0
    )

    val TAG = "MyScrollView"

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    var startY = 0
    var currentY = 0
    var isStarted = false
    var isStopped = false

    /**
     * 非日历区域滑动会回调至当前方法
     */
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent: $ev")
        val containerRelative: RelativeLayout = getChildAt(0) as RelativeLayout
        val viewPager2: ViewPager2 = containerRelative.getChildAt(1) as ViewPager2
        val adapter: HorizontalCalendarAdapter = viewPager2.adapter as HorizontalCalendarAdapter
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y.toInt()
                currentY = startY
                Log.d(TAG, "onTouchEvent: Down $startY")
                isStarted = false
                isStopped = false
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "onTouchEvent: Up $currentY")
                startY = 0
                currentY = 0
            }
        }
        return super.onTouchEvent(ev)
    }
}