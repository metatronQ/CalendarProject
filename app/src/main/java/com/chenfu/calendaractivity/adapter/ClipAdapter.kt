package com.chenfu.calendaractivity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chenfu.calendaractivity.R

class ClipAdapter(val context: Context, val datas: Array<String>) : RecyclerView.Adapter<ClipAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.recycler_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        parent.clipChildren = false
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_recycler, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = datas[position]
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}