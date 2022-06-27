package com.chenfu.calendaractivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.MainActivity
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.util.CalendarUtil
import com.chenfu.calendaractivity.view.CalendarChangeFrameLayout
import java.util.*

class HorizontalCalendarAdapter(private val context: Context, val callback: MainActivity.Callback) :
    RecyclerView.Adapter<HorizontalCalendarAdapter.HorizontalCalendarViewHolder>() {

    private val TAG = "HorizontalCalendar"
    private var mYear = 0

    // 1-12
    private var mMonth = 0
    private var mDay = 0

    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    private val monthList: ArrayList<List<CalendarUtil.DateInfo>> = ArrayList(3)
    private val weekList: ArrayList<List<CalendarUtil.DateInfo>> = ArrayList(3)
    private val mCalendarUtil = CalendarUtil()

    class HorizontalCalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val monthGridView: GridView = itemView.findViewById(R.id.month_grid)
        val weekGridView: GridView = itemView.findViewById(R.id.week_grid)
        val itemContainer: CalendarChangeFrameLayout =
            itemView.findViewById(R.id.calendar_container)
    }

    private var isMonth = true

    fun isMonth() = isMonth

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalCalendarViewHolder {
        initToday()
        // 需要在bind之前加载三月和三周的数据，绑定时只需要根据位置绑定即可
        initMonthDatas()
        initWeekDatas()
        return HorizontalCalendarViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_month_week, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HorizontalCalendarViewHolder, position: Int) {
        val view = holder.itemContainer
        setVerticalTouchEvent(view)
        val monthAdapter = object : TimeAdapter(context, monthList[position]) {
            override fun bindView(itemView: View, itemPosition: Int) {
                bindView(itemView, position, itemPosition)
            }
        }
        val weekAdapter = object : TimeAdapter(context, weekList[position]) {
            override fun bindView(itemView: View, itemPosition: Int) {
                bindView(itemView, position, itemPosition)
            }
        }
        holder.monthGridView.adapter = monthAdapter
        holder.weekGridView.adapter = weekAdapter
        holder.weekGridView.visibility = View.GONE
    }

    fun bindView(itemView: View, position: Int, itemPosition: Int) {
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
        }
        itemView.setOnClickListener {
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH) + 1
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
            updateSelect(monthList[position][itemPosition])
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    fun isSelectedDay(year: Int, month: Int, day: Int): Boolean {
        return selectedYear == year && selectedMonth == month && selectedDay == day
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setVerticalTouchEvent(view: CalendarChangeFrameLayout) {
        view.setOnTouchListener { _, event ->
            view.onTouchEvent(event)
        }
    }

    fun updateSelect(dateInfo: CalendarUtil.DateInfo) {
        initMonthDatas()
        initWeekDatas()
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        notifyDataSetChanged()
    }

    fun updateNext() {
        if (isMonth) {
            selectedDay = 1
            if (selectedMonth == 12) {
                selectedMonth = 1
                selectedYear += 1
            } else {
                selectedMonth += 1
                selectedYear = selectedYear
            }
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(selectedYear, selectedMonth - 1, selectedDay)
            calendar.add(Calendar.DAY_OF_MONTH, 7)
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH) + 1
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
        }
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        initMonthDatas()
        initWeekDatas()
        notifyDataSetChanged()
    }

    fun updatePre() {
        if (isMonth) {
            selectedDay = 1
            if (selectedMonth == 1) {
                selectedMonth = 12
                selectedYear -= 1
            } else {
                selectedMonth -= 1
                selectedYear = selectedYear
            }
        } else {
            val calendar = Calendar.getInstance()
            calendar.set(selectedYear, selectedMonth - 1, selectedDay)
            calendar.add(Calendar.DAY_OF_MONTH, -7)
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH) + 1
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
        }
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        initMonthDatas()
        initWeekDatas()
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