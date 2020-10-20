package com.example.demo.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.utils.CommonUtils
import kotlinx.android.synthetic.main.item_add_item_icon.view.*
import kotlinx.android.synthetic.main.item_add_item_source.view.*

class AddItemSourceAdapter(private var list: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectSource = list[0]
    private var onItemClickListener: ((source: String) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_item_source, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (selectSource == list[position]) {
            holder.itemView.tvSource.alpha = 1f
        } else {
            holder.itemView.tvSource.alpha = 0.2f
        }
        holder.itemView.tvSource.text = list[position]
        holder.itemView.setOnClickListener {
            selectSource = list[position]
            notifyDataSetChanged()
            onItemClickListener?.invoke(list[position])
        }
    }

    fun setOnItemClickListener(listener: (source: String) -> Unit) {
        onItemClickListener = listener
    }

    fun setSourceList(list: List<String>) {
        this.list = list
        for (i in list.indices){
            CommonUtils.e("source: ${list[i]}")
        }
        notifyDataSetChanged()
    }

    fun setSelectedSource(selectSource: String) {
        this.selectSource = selectSource
        notifyDataSetChanged()
    }

    fun getSelectedSource(): String {
        return selectSource
    }
}