package com.chenfu.calendaractivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class TimeAdapter(val context:Context, private var list: List<CalendarUtil.DateInfo>) : BaseAdapter() {
    
    val itemLayout = R.layout.item_time

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        // 0-6为第0行，7-13为第1行，以此类推
        return (position / 7).toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(itemLayout, parent, false)
        bindView(view, position)
        return view
    }

    /**
     * 交由外部具体设置item监听，减少参数传递
     */
    abstract fun bindView(itemView: View, itemPosition: Int)
}