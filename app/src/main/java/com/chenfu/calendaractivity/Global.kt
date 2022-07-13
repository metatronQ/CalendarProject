package com.chenfu.calendaractivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager2.widget.ViewPager2
import com.chenfu.calendaractivity.adapter.MonthCalendarAdapter
import com.chenfu.calendaractivity.adapter.WeekCalendarAdapter
import com.chenfu.calendaractivity.util.CalendarUtil
import com.chenfu.calendaractivity.util.DisplayUtils
import kotlin.math.abs

object Global {
    private val TAG = "animation"
    var selectedYear = 2022
    var selectedMonth = 1
    var selectedDay = 1

    var rawCount = 6
    var raw = 0
    var itemHeight = 0

    var animator: ValueAnimator? = null

    var isMonth = true
    var isAnimatorStarted = false

    private var isWeekChanged = false
    private var isPlaceHolderChange = false

    fun startAnimationForWeek(
        monthViewPager2: ViewPager2,
        weekViewPager2: ViewPager2,
        placeHolderContainer: LinearLayout
    ) {
        if (animator?.isStarted == true || animator?.isRunning == true) {
            return
        }
        raw = CalendarUtil().getRaw(selectedYear, selectedMonth - 1, selectedDay)
        rawCount = CalendarUtil().getAllRaws(selectedYear, selectedMonth - 1, selectedDay)
        val height = DisplayUtils.dip2px(monthViewPager2.context, 300f) / rawCount
        itemHeight = height

        val monthHeight = itemHeight * rawCount * 1f
        val displayTiming = -(raw - 1) * itemHeight * 1f
        animator = ValueAnimator.ofFloat(0f, displayTiming, itemHeight - monthHeight, -monthHeight)
        animator?.run {
            duration = 500
            interpolator = LinearInterpolator()
            addUpdateListener {
                val translationY = it.animatedValue as Float
                monthViewPager2.translationY = translationY
                Log.d(TAG, "startAnimationForWeek: $translationY")
                if (!isPlaceHolderChange) {
                    placeHolderContainer.translationY = translationY
                }
                Log.d(
                    TAG,
                    "startAnimationForWeek: placeholder translationY: ${placeHolderContainer.translationY}"
                )
                if (isAbsoluteLess(translationY, displayTiming, 10) && !isWeekChanged) {
                    weekViewPager2.visibility = View.VISIBLE
                    isWeekChanged = true
                    Log.d(TAG, "startAnimationForWeek: isWeekChanged: translationY $translationY")
                }
                if (isAbsoluteLess(
                        monthHeight + translationY,
                        itemHeight * 1f,
                        10
                    ) && !isPlaceHolderChange
                ) {
                    val lp = placeHolderContainer.layoutParams as RelativeLayout.LayoutParams
                    lp.removeRule(RelativeLayout.BELOW)
                    lp.addRule(RelativeLayout.BELOW, weekViewPager2.id)
                    placeHolderContainer.layoutParams = lp
                    isPlaceHolderChange = true
                    placeHolderContainer.translationY = 0f
                }
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    // 月切周，月数据已经确定，需要在开始动画前更新周的数据和视图
                    (weekViewPager2.adapter as WeekCalendarAdapter).updateSelect()
                    weekViewPager2.setCurrentItem(1, false)

                    monthViewPager2.visibility = View.VISIBLE
                    isAnimatorStarted = true
                    isWeekChanged = false
                    isPlaceHolderChange = false
                    Log.d(TAG, "raw = $raw, rawCount = $rawCount, itemHeight = $itemHeight")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    monthViewPager2.visibility = View.GONE
                    isAnimatorStarted = false
                    isMonth = false
                }
            })
            start()
        }
    }

    fun startAnimationForMonth(
        monthViewPager2: ViewPager2,
        weekViewPager2: ViewPager2,
        placeHolderContainer: LinearLayout
    ) {
        if (animator?.isStarted == true || animator?.isRunning == true) {
            return
        }
        raw = CalendarUtil().getRaw(selectedYear, selectedMonth - 1, selectedDay)
        rawCount = CalendarUtil().getAllRaws(selectedYear, selectedMonth - 1, selectedDay)
        val height = DisplayUtils.dip2px(monthViewPager2.context, 300f) / rawCount
        itemHeight = height

        val monthHeight = itemHeight * rawCount * 1f
        val displayTiming = -(raw - 1) * itemHeight * 1f
        animator = ValueAnimator.ofFloat(-monthHeight, itemHeight - monthHeight, displayTiming, 0f)
        animator?.run {
            duration = 500
            interpolator = LinearInterpolator()
            addUpdateListener {
                val translationY = it.animatedValue as Float
                monthViewPager2.translationY = translationY
                Log.d(TAG, "startAnimationForWeek: $translationY")
                if (isPlaceHolderChange) {
                    placeHolderContainer.translationY = translationY
                }
                Log.d(
                    TAG,
                    "startAnimationForWeek: placeholder translationY: ${placeHolderContainer.translationY}"
                )
                if (isAbsoluteLess(translationY, displayTiming, 10) && !isWeekChanged) {
                    weekViewPager2.visibility = View.GONE
                    isWeekChanged = true
                    Log.d(TAG, "startAnimationForWeek: isWeekChanged: translationY $translationY")
                }
                if (isAbsoluteLess(
                        monthHeight + translationY,
                        itemHeight * 1f,
                        10
                    ) && !isPlaceHolderChange
                ) {
                    val lp = placeHolderContainer.layoutParams as RelativeLayout.LayoutParams
                    lp.removeRule(RelativeLayout.BELOW)
                    lp.addRule(RelativeLayout.BELOW, monthViewPager2.id)
                    placeHolderContainer.layoutParams = lp
                    isPlaceHolderChange = true
                    placeHolderContainer.translationY = translationY
                }
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    // 周切月，周数据已经确定，需要在开始动画前更新月的数据和视图
                    (monthViewPager2.adapter as MonthCalendarAdapter).updateSelect()
                    monthViewPager2.setCurrentItem(1, false)
                    monthViewPager2.visibility = View.VISIBLE
                    isAnimatorStarted = true
                    isWeekChanged = false
                    isPlaceHolderChange = false
                    Log.d(TAG, "raw = $raw, rawCount = $rawCount, itemHeight = $itemHeight")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    isAnimatorStarted = false
                    isMonth = true
                }
            })
            start()
        }
    }

    /**
     * 5ms的动画，其value的变化较大，可能需要扩大比较的范围
     */
    fun isAbsoluteLess(translationY: Float, displayTiming: Float, value: Int): Boolean {
        if (translationY == displayTiming) {
            return true
        }
        val absoluteA = abs(translationY)
        val absoluteB = abs(displayTiming)
        if (absoluteA > absoluteB) {
            if (absoluteA - absoluteB < value) {
                return true
            }
        } else {
            if (absoluteB - absoluteA < value) {
                return true
            }
        }
        return false
    }

}