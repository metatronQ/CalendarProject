package com.chenfu.calendaractivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.Callback2Update
import com.chenfu.calendaractivity.MainActivity
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.view.CalendarChangeFrameLayout
import com.chenfu.calendaractivity.view.MyGridView

abstract class BaseAdapter(protected val context: Context, val callback: MainActivity.Callback) :
    RecyclerView.Adapter<BaseAdapter.HorizontalCalendarViewHolder>() {

    protected var callback2Update: Callback2Update? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalCalendarViewHolder {
        val holder = HorizontalCalendarViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_common_container, parent, false)
        )
        holder.itemContainer.setCallback(callback2Update)
        return holder
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: HorizontalCalendarViewHolder, position: Int) {
        // do nothing
    }

    abstract fun updatePre()

    abstract fun updateNext()

    fun setCallback2UpdateListener(callback: Callback2Update) {
        callback2Update = callback
    }

    class HorizontalCalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gridView: MyGridView = itemView.findViewById(R.id.common_grid)
        val itemContainer: CalendarChangeFrameLayout =
            itemView.findViewById(R.id.calendar_container)
    }
}