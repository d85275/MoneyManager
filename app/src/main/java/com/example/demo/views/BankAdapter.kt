package com.example.demo.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.BankData
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.views.history.BankViewHolder
import com.example.demo.views.history.BaseBankViewHolder
import com.example.demo.views.history.EmptyViewHolder

class BankAdapter : RecyclerView.Adapter<BaseBankViewHolder>() {
    private var alNames = arrayListOf<BankData?>(null)
    private lateinit var mainViewModel: MainViewModel

    private companion object {
        private const val EMPTY = 0
        private const val BANK = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBankViewHolder {
        return when (viewType) {
            EMPTY -> {
                val holder = EmptyViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bank_card_add, parent, false)
                    , mainViewModel
                )
                holder
            }
            else -> {
                BankViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bank_card, parent, false)
                    , mainViewModel
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return alNames.size
    }

    override fun onBindViewHolder(holder: BaseBankViewHolder, position: Int) {
        holder.bindView(position, alNames)
    }

    fun setViewModel(mainViewModel: MainViewModel) {
        this.mainViewModel = mainViewModel
    }

    fun setList(list: List<BankData?>) {
        alNames = list as ArrayList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (alNames[position] == null) EMPTY else BANK
    }
}