package com.example.demo.views.main.bank_frag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import kotlinx.android.synthetic.main.item_bank_color.view.*

class BankCardColorAdapter(val list: List<Int>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectIdx = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bank_color, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.ivColor.setBackgroundResource(list[position])
        if (selectIdx == position) {
            holder.itemView.ivColor.alpha = 1f
        } else {
            holder.itemView.ivColor.alpha = 0.2f
        }
        holder.itemView.setOnClickListener {
            selectIdx = position
            notifyDataSetChanged()
        }
    }

    fun setSelectedIdx(selectIdx:Int){
        this.selectIdx = selectIdx
    }

    fun getSelectedColor(): Int {
        return list[selectIdx]
    }
}