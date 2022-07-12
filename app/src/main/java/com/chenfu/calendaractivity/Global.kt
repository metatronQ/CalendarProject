package com.chenfu.calendaractivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.viewpager2.widget.ViewPager2
import com.chenfu.calendaractivity.adapter.MonthCalendarAdapter
import com.chenfu.calendaractivity.adapter.WeekCalendarAdapter

object Global {
    private val TAG = "animation"
    var selectedYear = 0
    var selectedMonth = 0
    var selectedDay = 0

    var raw = 0
    var itemHeight = 0

    var animator: ValueAnimator? = null


    fun startAnimationForWeek(monthViewPager2: ViewPager2, weekViewPager2: ViewPager2) {
        Log.d(TAG, "raw = $raw, itemHeight = $itemHeight")
        if (animator?.isStarted == true|| animator?.isRunning == true) {
            return
        }
        animator = ValueAnimator.ofFloat(0f, -6 * itemHeight * 1f)
        animator?.let{
            it.duration = 5000
            it.interpolator = LinearInterpolator()
            it.addUpdateListener {
                val displayTiming = -(raw - 1) * itemHeight * 1f
                val translationY = it.animatedValue as Float
                Log.d(TAG, "startAnimationForWeek: $translationY")
                monthViewPager2.translationY = translationY
                if (isAbsoluteLess10(translationY, displayTiming)) {
                    (weekViewPager2.adapter as WeekCalendarAdapter).updateSelect()
                    weekViewPager2.setCurrentItem(1, false)
                    weekViewPager2.visibility = View.VISIBLE
                }
            }
            it.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    monthViewPager2.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    monthViewPager2.visibility = View.GONE
                }
            })
            it.start()
        }
    }

    fun startAnimationForMonth(monthViewPager2: ViewPager2, weekViewPager2: ViewPager2) {
        if (animator?.isStarted == true|| animator?.isRunning == true) {
            return
        }
        animator = ValueAnimator.ofFloat(-6 * itemHeight * 1f, 0f)
        animator?.let {
            it.duration = 5000
            it.interpolator = LinearInterpolator()
            it.addUpdateListener {
                val displayTiming = -(raw - 1) * itemHeight * 1f
                val translationY = it.animatedValue as Float
                Log.d(TAG, "startAnimationForWeek: $translationY")
                monthViewPager2.translationY = translationY
                if (isAbsoluteLess10(translationY, displayTiming)) {
                    weekViewPager2.visibility = View.GONE
                }
            }
            it.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    (monthViewPager2.adapter as MonthCalendarAdapter).updateSelect()
                    monthViewPager2.setCurrentItem(1, false)
                    monthViewPager2.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) {
                    // do nothing
                }
            })
            it.start()
        }
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

}