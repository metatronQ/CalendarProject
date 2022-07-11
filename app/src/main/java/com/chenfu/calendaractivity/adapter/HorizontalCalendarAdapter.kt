package com.chenfu.calendaractivity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.GlobalField
import com.chenfu.calendaractivity.MainActivity
import com.chenfu.calendaractivity.R
import com.chenfu.calendaractivity.util.CalendarUtil
import com.chenfu.calendaractivity.util.DisplayUtils
import com.chenfu.calendaractivity.view.CalendarChangeFrameLayout
import com.chenfu.calendaractivity.view.MyGridView
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
        val monthGridView: MyGridView = itemView.findViewById(R.id.month_grid)
        val weekGridView: MyGridView = itemView.findViewById(R.id.week_grid)
        val itemContainer: CalendarChangeFrameLayout =
            itemView.findViewById(R.id.calendar_container)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalCalendarViewHolder {
        initToday()
        // 需要在bind之前加载三月和三周的数据，绑定时只需要根据位置绑定即可
        initMonthDatas()
        initWeekDatas()
        val holder = HorizontalCalendarViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_month_week, parent, false)
        )
        // 初始时隐藏
        holder.weekGridView.visibility = View.GONE
        // 初始时设置callback
        holder.itemContainer.setCallback(object : Callback2Visibility {
            override fun updateVisibility() {
                notifyDataSetChanged()
            }
        })
        return holder
    }

    override fun onBindViewHolder(holder: HorizontalCalendarViewHolder, position: Int) {
        setVerticalTouchEvent(holder.itemContainer)
        val lastPosition = holder.adapterPosition
        val raw = monthList[lastPosition].size / 7
        val height = DisplayUtils.dip2px(context, 300f) / raw
        val monthAdapter = object : TimeAdapter(context, monthList[lastPosition]) {
            override fun bindView(itemView: View, itemPosition: Int) {
                val layout: FrameLayout = itemView.findViewById(R.id.item_container)
//                layout.measure(
//                    layout.measuredWidth,
//                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
//                )
                val params = layout.layoutParams
                params.height = height
                layout.layoutParams = params
                bindView(itemView, lastPosition, itemPosition)
            }
        }
        val weekAdapter = object : TimeAdapter(context, weekList[lastPosition]) {
            override fun bindView(itemView: View, itemPosition: Int) {
                bindView(itemView, lastPosition, itemPosition)
            }
        }
        holder.monthGridView.adapter = monthAdapter
//        holder.monthGridView.setDayCount(monthList[lastPosition].size)
        holder.weekGridView.adapter = weekAdapter
//        holder.weekGridView.setDayCount(7)
        if (GlobalField.isMonth) {
            holder.monthGridView.visibility = View.VISIBLE
            holder.weekGridView.visibility = View.GONE
        } else {
            holder.monthGridView.visibility = View.GONE
            holder.weekGridView.visibility = View.VISIBLE
        }
        // TODO: ????
        holder.itemContainer.setWeekRowPosition(calculateRow(), height.toFloat())
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

    fun bindView(itemView: View, position: Int, itemPosition: Int) {
        val date = if (GlobalField.isMonth) {
            monthList[position][itemPosition].date
        } else {
            weekList[position][itemPosition].date
        }
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
            updateSelect()
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

    fun updateSelect() {
        initMonthDatas()
        initWeekDatas()
        callback.setTvYearAndMonth(selectedYear, selectedMonth)
        // 可能会点击到上下月而进行三月切换，若要优化则需要判断点击是否在当前月
        notifyDataSetChanged()
    }

    fun updateNext() {
        if (GlobalField.isMonth) {
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
        if (GlobalField.isMonth) {
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

    interface Callback2Visibility {
        fun updateVisibility()
    }
}