package com.example.demo.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.HistoryData
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var alHistoryData = arrayListOf<HistoryData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return object :RecyclerView.ViewHolder(view){}
    }

    override fun getItemCount(): Int {
        return alHistoryData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = alHistoryData[position].name
        holder.itemView.tvType.text = alHistoryData[position].type
        holder.itemView.tvPrice.text = "$ ${alHistoryData[position].price}"
    }

    fun setList(list:List<HistoryData>){
        alHistoryData = list as ArrayList
        notifyDataSetChanged()
    }
}