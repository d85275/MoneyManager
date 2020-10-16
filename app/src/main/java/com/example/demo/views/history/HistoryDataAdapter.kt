package com.example.demo.views.history

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.HistoryData
import com.example.demo.model.HistoryData.Companion.TYPE_INCOME
import com.example.demo.utils.CommonUtils
import kotlinx.android.synthetic.main.item_history_data.view.*
import java.lang.StringBuilder

class HistoryDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var alHistoryData = arrayListOf<HistoryData>()
    private var isEditMode = false
    private val selectedId = MutableLiveData(hashSetOf<Int>())
    var onItemClick: ((HistoryData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_data, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun getItemCount(): Int {
        return alHistoryData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tvName.text = alHistoryData[position].name
        /*
        if (position == 0) {
            holder.itemView.vUp.visibility = View.INVISIBLE
        } else if (position == alHistoryData.lastIndex) {
            holder.itemView.vDown.visibility = View.INVISIBLE
        }
         */
        if (alHistoryData[position].source == HistoryData.SOURCE_CASH) {
            holder.itemView.tvSource.text = holder.itemView.context.getString(R.string.cash)
        } else {
            holder.itemView.tvSource.text = alHistoryData[position].source
        }

        holder.itemView.tvPrice.text = getPrice(position)
        if (alHistoryData[position].type == TYPE_INCOME) {
            holder.itemView.tvName.setTextColor(getDarkColor())
            holder.itemView.tvSource.setTextColor(getLightColor())
            holder.itemView.tvPrice.setTextColor(getLightColor())
        }
        if (isEditMode) {
            holder.itemView.ivImage.visibility = View.INVISIBLE
            holder.itemView.cbDelete.visibility = View.VISIBLE
        } else {
            holder.itemView.ivImage.visibility = View.VISIBLE
            holder.itemView.cbDelete.visibility = View.INVISIBLE
        }
        holder.itemView.setOnClickListener {
            if (isEditMode) {
                return@setOnClickListener
            }
            onItemClick?.invoke(alHistoryData[position])
        }
        holder.itemView.cbDelete.isChecked = false
        holder.itemView.cbDelete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                CommonUtils.e("add $position to the set")
                val set = selectedId.value!!
                set.add(position)
                selectedId.value = set
            } else {
                CommonUtils.e("remove $position to the set")
                val set = selectedId.value!!
                set.remove(position)
                selectedId.value = set
            }
        }
    }

    private fun getPrice(position: Int): String {
        val price = StringBuilder().append("$")
        if (alHistoryData[position].type == HistoryData.TYPE_EXPENSE) {
            price.append(" -")
        }
        price.append(alHistoryData[position].price)
        return price.toString()
    }

    fun getSelectedId(): LiveData<HashSet<Int>> {
        return selectedId
    }

    fun setIsEditMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        selectedId.value?.clear()
        notifyDataSetChanged()
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