package com.example.demo.views.history

import android.view.View
import com.example.demo.model.BankData
import com.example.demo.utils.CommonUtils
import com.example.demo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.item_bank_card_add.view.*

class EmptyViewHolder(itemView: View, mainViewModel: MainViewModel) :
    BaseBankViewHolder(itemView, mainViewModel) {

    override fun bindView(position: Int, list: List<BankData?>) {
        itemView.ivAddBank.setOnClickListener {
            CommonUtils.showAddBankDialog(itemView, mainViewModel)
        }
    }
}