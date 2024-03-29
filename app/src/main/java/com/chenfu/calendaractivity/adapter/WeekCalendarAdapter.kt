package com.chenfu.calendaractivity.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chenfu.calendaractivity.GlobalInstance.selectedDay
import com.chenfu.calendaractivity.GlobalInstance.selectedMonth
import com.chenfu.calendaractivity.GlobalInstance.selectedYear
import com.chenfu.calendaractivity.MainActivity
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.util.CalendarUtil
import com.chenfu.calendaractivity.util.DisplayUtils
import java.util.*

class WeekCalendarAdapter(context: Context, callback: MainActivity.Callback) :
    BaseAdapter(context, callback) {

    private val TAG = "WeekCalendarAdapter"
    private var mYear = 0

    // 1-12
    private var mMonth = 0
    private var mDay = 0

    private val weekList: ArrayList<List<CalendarUtil.DateInfo>> = ArrayList(3)
    private val mCalendarUtil = CalendarUtil()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalCalendarViewHolder {
        // 需要在bind之前加载三周的数据，绑定时只需要根据位置绑定即可
        initWeekDatas()
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: HorizontalCalendarViewHolder, position: Int) {
        val lastPosition = holder.adapterPosition
        // week先对每一个pager的高度都设定为当前选中的日期所对应的高度，避免切换时有空隙，切换后会根据计算后的选中日期重新设置所有item即每个pager的高度
        val raws = CalendarUtil().getAllRaws(selectedYear, selectedMonth - 1, selectedDay)
        val height = DisplayUtils.dip2px(context, 300f) / raws
        val lp = holder.gridView.layoutParams
        lp.height = height
        holder.gridView.layoutParams = lp
        val weekAdapter = object : TimeAdapter(context, weekList[lastPosition]) {
            override fun bindView(itemView: View, itemPosition: Int) {
                bindView(itemView, lastPosition, itemPosition, height)
            }
        }
        holder.gridView.adapter = weekAdapter
    }

    fun bindView(itemView: View, position: Int, itemPosition: Int, height: Int) {
        val params = itemView.layoutParams
        params.height = height
        itemView.layoutParams = params

        val date = weekList[position][itemPosition].date
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        val calendar = Calendar.getInstance()
        calendar.time = date
        tvDay.text = "${calendar.get(Calendar.DAY_OF_MONTH)}"
        if (isSelectedDay(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        ) {
            itemView.setBackgroundColor(Color.RED)
            Log.d(TAG, "bindView: ${tvDay.text}")
        }
        itemView.setOnClickListener {
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH) + 1
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
            updateSelect()
        }
    }

    fun isSelectedDay(year: Int, month: Int, day: Int): Boolean {
        return selectedYear == year && selectedMonth == month && selectedDay == day
    }

    fun updateSelect() {
        initWeekDatas()
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        // 可能会点击到上下月而进行三月切换，若要优化则需要判断点击是否在当前月
        notifyDataSetChanged()
    }

    override fun updateNext() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth - 1, selectedDay)
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        selectedYear = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH) + 1
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        initWeekDatas()
        notifyDataSetChanged()
    }

    override fun updatePre() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth - 1, selectedDay)
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        selectedYear = calendar.get(Calendar.YEAR)
        selectedMonth = calendar.get(Calendar.MONTH) + 1
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        initWeekDatas()
        notifyDataSetChanged()
    }

    fun initWeekDatas() {
        weekList.clear()
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth - 1, selectedDay)
        val preCalendar = calendar.clone() as Calendar
        preCalendar.add(Calendar.DAY_OF_MONTH, -7)
        val afterCalendar = calendar.clone() as Calendar
        afterCalendar.add(Calendar.DAY_OF_MONTH, 7)
        weekList.add(mCalendarUtil.initWeekData(preCalendar.time))
        weekList.add(mCalendarUtil.initWeekData(calendar.time))
        weekList.add(mCalendarUtil.initWeekData(afterCalendar.time))
    }
}