package com.example.demo.views

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.model.HistoryData.Companion.TYPE_INCOME
import kotlinx.android.synthetic.main.item_history_data.view.*
import kotlinx.android.synthetic.main.item_recent_data.view.*
import kotlinx.android.synthetic.main.item_recent_data.view.tvName
import kotlinx.android.synthetic.main.item_recent_data.view.tvPrice

class HistoryDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var alHistoryData = arrayListOf<HistoryData>()
    var onItemClick: ((HistoryData) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_data, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return alHistoryData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = alHistoryData[position].name
        holder.itemView.tvSource.text = alHistoryData[position].source
        holder.itemView.tvPrice.text = "$ ${alHistoryData[position].price}"
        if (alHistoryData[position].type == TYPE_INCOME) {
            holder.itemView.tvName.setTextColor(getDarkColor())
            holder.itemView.tvDate.setTextColor(getLightColor())
            holder.itemView.tvPrice.setTextColor(getLightColor())
        }
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(alHistoryData[position])
        }
    }

    fun setList(list: List<HistoryData>) {
        alHistoryData = list as ArrayList
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