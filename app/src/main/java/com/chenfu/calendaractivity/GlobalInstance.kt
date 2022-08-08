package com.chenfu.calendaractivity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object GlobalInstance {
    private val TAG = "GlobalInstance"

    private val translationY = "translationY"

    var selectedYear = 2022
    var selectedMonth = 1
    var selectedDay = 1

    var isMonth = true
    var isAnimatorStarted = false

    private val MES_MONTH_TO_WEEK_UPDATE = 1
    private val MES_WEEK_TO_MONTH_UPDATE = 2

    private var updateListener: UpdateListener? = null

    private val valueHandler = Handler(Looper.getMainLooper()!!) {
        when(it.what) {
            MES_MONTH_TO_WEEK_UPDATE -> {
                updateListener?.updateMonth2Week(it.data.getFloat(translationY))
            }
            MES_WEEK_TO_MONTH_UPDATE -> {
                updateListener?.updateWeek2Month(it.data.getFloat(translationY))
            }
        }
        true
    }

    private var disposableOfTimer: Disposable? = null

    /**
     * 调用于开始之前
     */
    fun setListener(callback: UpdateListener) {
        updateListener = callback
    }

    fun startAnimationForWeek(initialY: Float, monthHeight: Float, duration: Int) {
        isMonth = true
        isAnimatorStarted = false
        val intervalY = abs(monthHeight) / duration
        var startTime = 0
        var updateHeight = initialY
        disposableOfTimer = Observable.interval(1, TimeUnit.MILLISECONDS)
            .takeWhile {
                startTime <= duration
            }
            .observeOn(Schedulers.computation())
            .subscribe {
                if (startTime >= duration) {
                    Log.d(TAG, "startAnimationForWeek: end $updateHeight startTime $startTime")
                    isMonth = false
                    isAnimatorStarted = false
                    disposableOfTimer?.dispose()
                    startTime++
                    return@subscribe
                }
                isAnimatorStarted = true
                updateHeight -= intervalY
                val message = Message.obtain()
                message.what = MES_MONTH_TO_WEEK_UPDATE
                val bundle = Bundle()
                bundle.putFloat(translationY, updateHeight)
                message.data = bundle
                valueHandler.sendMessage(message)
                Log.d(TAG, "startAnimationForWeek: $updateHeight")
                startTime++
            }
    }

    fun startAnimationForMonth(initialY: Float, monthHeight: Float, duration: Int) {
        isMonth = false
        isAnimatorStarted = false
        val intervalY = abs(monthHeight) / duration
        var startTime = 0
        var updateHeight = initialY
        disposableOfTimer = Observable.interval(1, TimeUnit.MILLISECONDS)
            .takeWhile {
                startTime <= duration
            }
            .observeOn(Schedulers.computation())
            .subscribe {
                if (startTime >= duration) {
                    Log.d(TAG, "startAnimationForMonth: end $updateHeight startTime $startTime")
                    isMonth = true
                    isAnimatorStarted = false
                    disposableOfTimer?.dispose()
                    startTime++
                    return@subscribe
                }
                isAnimatorStarted = true
                updateHeight += intervalY
                val message = Message.obtain()
                message.what = MES_WEEK_TO_MONTH_UPDATE
                val bundle = Bundle()
                bundle.putFloat(translationY, updateHeight)
                message.data = bundle
                valueHandler.sendMessage(message)
                Log.d(TAG, "startAnimationForMonth: $updateHeight")
                startTime++
            }
    }

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

    fun release() {
        valueHandler.removeCallbacksAndMessages(null)
        if (disposableOfTimer != null && !disposableOfTimer!!.isDisposed) {
            disposableOfTimer!!.dispose()
        }
    }

    interface UpdateListener {
        fun updateMonth2Week(translationY: Float)
        fun updateWeek2Month(translationY: Float)
    }
}