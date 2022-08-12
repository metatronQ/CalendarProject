package com.chenfu.calendaractivity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.util.DisplayUtils

class MyItemDecoration(private val context: Context, private val data: Array<String>) : RecyclerView.ItemDecoration() {
    val mPaint by lazy {
        val paint = Paint()
//        paint.color = Color.argb((0.2 * 256).toInt(), 176, 190, 197)
        paint.color = Color.RED
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = DisplayUtils.dip2px(context, 1f).toFloat()
        return@lazy paint
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(DisplayUtils.dip2px(context, 12f), 0, 0, DisplayUtils.dip2px(context, 24f))
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childLayoutPosition = parent.getChildLayoutPosition(child)
            if (childLayoutPosition == RecyclerView.NO_POSITION || childLayoutPosition == data.size - 1) {
                continue
            }
            val yMargin = (child.bottom - child.top).toFloat() / 4
            val x = child.left.toFloat() - DisplayUtils.dip2px(context, 6f)
            val startY: Float = child.top + yMargin * 3
            val stopY: Float = child.bottom.toFloat() + yMargin + DisplayUtils.dip2px(context, 24f)
            c.drawLine(x, startY, x, stopY, mPaint)
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

}