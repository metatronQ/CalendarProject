package com.chenfu.calendaractivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.chenfu.calendaractivity.Global.itemHeight
import com.chenfu.calendaractivity.Global.raw
import com.chenfu.calendaractivity.Global.selectedDay
import com.chenfu.calendaractivity.Global.selectedMonth
import com.chenfu.calendaractivity.Global.selectedYear
import com.chenfu.calendaractivity.MainActivity
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.util.CalendarUtil
import com.chenfu.calendaractivity.util.DisplayUtils
import com.chenfu.calendaractivity.view.CalendarChangeFrameLayout
import java.util.*

class MonthCalendarAdapter(context: Context, callback: MainActivity.Callback) : BaseAdapter(context, callback){

    private val TAG = "HorizontalCalendar"
    private var mYear = 0

    // 1-12
    private var mMonth = 0
    private var mDay = 0

    private val monthList: ArrayList<List<CalendarUtil.DateInfo>> = ArrayList(3)
    private val mCalendarUtil = CalendarUtil()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalCalendarViewHolder {
        initToday()
        // 需要在bind之前加载三月的数据，绑定时只需要根据位置绑定即可
        initMonthDatas()
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: HorizontalCalendarViewHolder, position: Int) {
        setVerticalTouchEvent(holder.itemContainer)
        val lastPosition = holder.adapterPosition
        val raws = monthList[lastPosition].size / 7
        val height = DisplayUtils.dip2px(context, 300f) / raws
        val monthAdapter = object : TimeAdapter(context, monthList[lastPosition]) {
            override fun bindView(itemView: View, itemPosition: Int) {
                bindView(itemView, lastPosition, itemPosition, height)
            }
        }
        holder.gridView.adapter = monthAdapter
    }

    fun calculateRow(): Int {
        var positionInList = 0
        val curMonth = monthList[1]
        val calendar = Calendar.getInstance()
        for (i in 0..curMonth.size) {
            calendar.time = curMonth[i].date
            // selectedDay肯定是当月的，若点击上下月会将当月更新的
            // 视图内可能含有相同的day，则需要判断不同月
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            if (day == selectedDay && month == selectedMonth) {
                positionInList = i
                break
            }
        }
        // 0-6 是第一行
        return positionInList / 7 + 1
    }

    fun bindView(itemView: View, position: Int, itemPosition: Int, height: Int) {
        val params = itemView.layoutParams
        params.height = height
        itemView.layoutParams = params
        itemHeight = height

        val date = monthList[position][itemPosition].date
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
            raw = CalendarUtil().getRaw(selectedYear, selectedMonth - 1, selectedDay)
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setVerticalTouchEvent(view: CalendarChangeFrameLayout) {
        view.setOnTouchListener { _, event ->
            view.onTouchEvent(event)
        }
    }

    fun updateSelect() {
        initMonthDatas()
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        // 可能会点击到上下月而进行三月切换，若要优化则需要判断点击是否在当前月
        notifyDataSetChanged()
    }

    override fun updateNext() {
        selectedDay = 1
        if (selectedMonth == 12) {
            selectedMonth = 1
            selectedYear += 1
        } else {
            selectedMonth += 1
            selectedYear = selectedYear
        }
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        initMonthDatas()
        notifyDataSetChanged()
    }

    override fun updatePre() {
        selectedDay = 1
        if (selectedMonth == 1) {
            selectedMonth = 12
            selectedYear -= 1
        } else {
            selectedMonth -= 1
            selectedYear = selectedYear
        }
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        initMonthDatas()
        notifyDataSetChanged()
    }

    fun initToday() {
        mYear = Calendar.getInstance().get(Calendar.YEAR)
        mMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        selectedYear = mYear
        selectedMonth = mMonth
        selectedDay = mDay

        callback.setTvYearAndMonth(selectedYear, selectedMonth)
    }

    fun initMonthDatas() {
        monthList.clear()
        var tempYear = selectedYear
        var tempMonth = selectedMonth
        var tempDay = selectedDay
        // 上一月
        if (selectedMonth == 1) {
            tempMonth = 12
            tempYear = selectedYear - 1
        } else {
            tempMonth = selectedMonth - 1
            tempYear = selectedYear
        }
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(tempYear, tempMonth - 1, tempDay)
        monthList.add(mCalendarUtil.initMonthData(calendar.time))
        // 当前月
        calendar.set(selectedYear, selectedMonth - 1, selectedDay)
        monthList.add(mCalendarUtil.initMonthData(calendar.time))
        // 下一月
        if (selectedMonth == 12) {
            tempMonth = 1
            tempYear = selectedYear + 1
        } else {
            tempMonth = selectedMonth + 1
            tempYear = selectedYear
        }
        calendar.set(tempYear, tempMonth - 1, tempDay)
        monthList.add(mCalendarUtil.initMonthData(calendar.time))
    }
}