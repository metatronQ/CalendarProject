package com.chenfu.calendaractivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.HorizontalVpAdapter.*
import java.util.*

class HorizontalVpAdapter(private val context: Context) : RecyclerView.Adapter<HorizontalVpViewHolder>() {
    val contentList: ArrayList<Int> = ArrayList<Int>(3)

    class HorizontalVpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_test)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalVpViewHolder {
        contentList.clear()
        contentList.add(0)
        contentList.add(1)
        contentList.add(2)
        return HorizontalVpViewHolder(LayoutInflater.from(context).inflate(R.layout.item_test, parent, false))
    }

    override fun onBindViewHolder(holder: HorizontalVpViewHolder, position: Int) {
        holder.textView.text = "${contentList[position]}"
    }

    override fun getItemCount(): Int {
        return 3
    }

    fun updateNext() {
        contentList[0]++
        contentList[1]++
        contentList[2]++
        notifyDataSetChanged()
    }

    fun updatePre() {
        contentList[0]--
        contentList[1]--
        contentList[2]--
        notifyDataSetChanged()
    }
}