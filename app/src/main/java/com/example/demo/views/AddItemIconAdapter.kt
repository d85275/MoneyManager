package com.example.demo.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import kotlinx.android.synthetic.main.item_add_item_icon.view.*

class AddItemIconAdapter(val list: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectPosition = 0
    private var onItemClickListener: ((icon: Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_item_icon, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.ivIcon.setBackgroundResource(list[position])
        if (selectPosition == position) {
            holder.itemView.ivIcon.alpha = 1f
        } else {
            holder.itemView.ivIcon.alpha = 0.2f
        }
        holder.itemView.setOnClickListener {
            selectPosition = position
            notifyDataSetChanged()
            onItemClickListener?.invoke(list[position])
        }
    }

    fun setOnItemClickListener(listener: (icon: Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setSelectedIdx(selectIdx: Int) {
        this.selectPosition = selectIdx
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return selectPosition
    }
}