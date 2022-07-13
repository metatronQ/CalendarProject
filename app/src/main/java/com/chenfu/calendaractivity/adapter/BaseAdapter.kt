package com.chenfu.calendaractivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.MainActivity
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.view.MyGridView

abstract class BaseAdapter(protected val context: Context, val callback: MainActivity.Callback) :
    RecyclerView.Adapter<BaseAdapter.HorizontalCalendarViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalCalendarViewHolder {
        val holder = HorizontalCalendarViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_common_container, parent, false)
        )
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

    class HorizontalCalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gridView: MyGridView = itemView.findViewById(R.id.common_grid)
        val itemContainer: FrameLayout =
            itemView.findViewById(R.id.calendar_container)
    }
}