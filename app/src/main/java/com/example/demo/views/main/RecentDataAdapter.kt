package com.example.demo.views.main

import android.graphics.Color
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.model.HistoryData.Companion.TYPE_INCOME
import kotlinx.android.synthetic.main.item_recent_data.view.*
import kotlinx.android.synthetic.main.item_recent_data.view.ivImage
import kotlinx.android.synthetic.main.item_recent_data.view.tvName
import kotlinx.android.synthetic.main.item_recent_data.view.tvPrice
import kotlinx.android.synthetic.main.item_recent_data.view.tvSource
import java.lang.StringBuilder

class RecentDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var alHistoryData = arrayListOf<HistoryData>()
    private var alIcons = arrayListOf<Int>()
    var onItemClick: ((HistoryData) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recent_data, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return alHistoryData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = alHistoryData[position].name
        holder.itemView.tvDate.text = alHistoryData[position].date
        holder.itemView.ivImage.setImageResource(alIcons[alHistoryData[position].iconPosition])
        val price = StringBuilder().append("$")
        if (alHistoryData[position].source == HistoryData.SOURCE_CASH) {
            holder.itemView.tvSource.text = holder.itemView.context.getString(R.string.cash)
        } else {
            holder.itemView.tvSource.text = alHistoryData[position].source
        }
        if (alHistoryData[position].type == HistoryData.TYPE_EXPENSE) {
            price.append(" -")
        }
        price.append(alHistoryData[position].price)
        holder.itemView.tvPrice.text = price.toString()
        if (alHistoryData[position].type == TYPE_INCOME) {
            holder.itemView.tvName.setTextColor(getDarkColor())
            holder.itemView.tvDate.setTextColor(getLightColor())
            holder.itemView.tvPrice.setTextColor(getLightColor())
        }

        setListeners(holder.itemView, position)
    }

    private fun setListeners(itemView: View, position: Int) {
        itemView.setOnClickListener {
            onItemClick?.invoke(alHistoryData[position])
        }
    }

    fun setList(list: List<HistoryData>, iconList: List<Int>) {
        alHistoryData = list as ArrayList
        alIcons = iconList as ArrayList
        notifyDataSetChanged()
    }

    fun setType(type: Int) {
        this.type = type
    }

    private var type = 0
    private fun getDarkColor(): Int {
        return if (type == 0) Color.parseColor("#93513f") else Color.parseColor("#4a7c51")
    }

    private fun getLightColor(): Int {
        return if (type == 0) Color.parseColor("#b87463") else Color.parseColor("#77127100")
    }
}