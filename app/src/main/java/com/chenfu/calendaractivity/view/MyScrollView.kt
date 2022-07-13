package com.chenfu.calendaractivity.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView
import com.chenfu.calendaractivity.Callback2Update
import com.chenfu.calendaractivity.Global

/**
 * 分发处理整个日历事件的关键
 *  1.若动画正在进行，则拦截包括down事件的所有事件
 *  2.不拦截down事件，可以使用内部拦截法child类girdview拦截其item的点击事件
 *  3.不拦截水平滑动事件
 *  4.根据onTouchEvent拦截竖直滑动事件，当scrollY=0时处理日历切换，不等于0时处理滑动
 *  5.content的点击/滑动事件
 *      1.可以跟girdview一样使用requestDisallowInterceptTouchEvent拦截点击/滑动事件，可以定义一个公共的可以拦截点击/滑动事件的父类
 *          -> 若存在滑动冲突，则必须使用此种方法
 *      2.scrollView直接对点击和scroll滑动进行区别拦截：
 *          down拦截：要返回super.onInterceptTouchEvent(ev)
 *              ->返回false可能将导致down事件被child消费从而导致scroll不能滑动
 *              ->返回true会拦截所有事件，child将无法接收到事件
 *          move拦截：点击不拦截，水平滑动不拦截，竖直滑动根据onTouchEvent的返回值拦截
 *          up拦截：返回false，拦截up将导致child不能消费点击事件
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
    private var callback: Callback2Update? = null
    var startY = 0
    var currentY = 0
    var startX = 0
    var currentX = 0
    var isNeedIntercept = true

    /**
     * 点击到container中的可消费事件的子View（如点击、滑动）会回调当前方法，用于判断是否拦截
     * 对于down事件：若都不拦截，即返回false，则会将down事件的处理传给child，即落点就不在scroll了
     *  1。若其余事件都返回true，则为scroll拦截后续全部事件，因此需要child使用requestDisallowInterceptTouchEvent自己拦截事件
     *  2。若其余事件都返回false，需要进行判断
     *      1。若要支持scroll滑动的同时需要支持child的点击事件（child不使用requestDisallowInterceptTouchEvent），则需要将down事件交给父类判断，这样保证scroll可能会拦截down事件
     *      否则down将会只落在child，而child又不进行允许parent拦截的操作，即使scroll消费了后续事件依旧无法正常滑动 TODO:根据现象解释，确实没搞懂这里
     *      2。拦截了后续任一事件child都将无法消费
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (Global.isAnimatorStarted) {
            // 若动画正在进行，则需要把down事件也拦截掉
            return true
        }
        Log.d(TAG, "onInterceptTouchEvent: $ev")
        val action = ev!!.action
        if (action == MotionEvent.ACTION_DOWN) {
            startX = ev.x.toInt()
            startY = ev.y.toInt()
            currentX = startX
            currentY = ev.y.toInt()
            isNeedIntercept = true
            return super.onInterceptTouchEvent(ev)
        }
        // 若是move事件则交给onTouchEvent，如果是竖直滑动则会返回true处理并拦截
        // 如果是点击事件、水平滑动事件，则就会一直返回false而不拦截了
        // 若存在滑动冲突，则必须创建自定义View配合使用
        if (action == MotionEvent.ACTION_MOVE) {
            currentX = ev.x.toInt()
            currentY = ev.y.toInt()
            Log.d(TAG, "onInterceptTouchEvent: Move X:$currentX Y:$currentY")
            if (startY - currentY < 100 && currentY - startY < 100 && startX - currentX < 100 && currentX - startX < 100) {
                // 统一不拦截点击事件
                return false
            }
            if (startX - currentX > 100 || currentX - startX > 100) {
                // 不拦截水平滑动
                isNeedIntercept = false
                return false
            }
            if (isNeedIntercept) return onTouchEvent(ev)
        }
        return false
    }

    /**
     * 当滑动或点击的区域没有可消费的childview，会直接回调此方法
     * 当拦截返回true时，也会跳过onInterceptTouchEvent直接回调此方法
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent: $event")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startY = event.y.toInt()
                currentY = startY
                Log.d(TAG, "onTouchEvent: Down $startY")
            }
            MotionEvent.ACTION_MOVE -> {
                currentY = event.y.toInt()
                if (Global.isAnimatorStarted) {
                    return true
                }
                if (scrollY == 0 && Global.isMonth) {
                    if (startY - currentY > 100) {
                        callback?.start2Week()
                    }
                    // 返回true表示消费和拦截
                    return true
                }
                if (scrollY == 0 && !Global.isMonth) {
                    // 只有处于0并且往月滑，scroll才不能滑动，其余情况交给scroll
                    if (currentY - startY > 100) {
                        callback?.start2Month()
                        return true
                    }
                }
                Log.d(TAG, "onTouchEvent: Move $currentY")
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "onTouchEvent: Up $currentY")
                startY = 0
                currentY = 0
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