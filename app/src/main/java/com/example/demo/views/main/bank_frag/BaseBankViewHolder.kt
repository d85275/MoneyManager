package com.example.demo.views.main.bank_frag

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.model.BankData
import com.example.demo.viewmodels.MainViewModel

open class BaseBankViewHolder(itemView: View, val mainViewModel: MainViewModel) :
    RecyclerView.ViewHolder(itemView) {
    open fun bindView(position: Int, list: List<BankData?>) {}
}