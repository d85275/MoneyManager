package com.example.demo.views.main.bank_frag

import android.view.View
import com.example.demo.R
import com.example.demo.model.BankData
import com.example.demo.utils.CommonUtils
import com.example.demo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.item_bank_card.view.*

class BankViewHolder(itemView: View, mainViewModel: MainViewModel) :
    BaseBankViewHolder(itemView, mainViewModel) {
    override fun bindView(position: Int, list: List<BankData?>) {
        itemView.clBackground.setBackgroundResource(
            list[position]?.color ?: R.drawable.icon_bank_green
        )
        itemView.tvName.text = list[position]?.name
        itemView.vEdit.setOnClickListener {
            CommonUtils.showEditBankDialog(
                itemView,
                mainViewModel,
                list[position],
                list[position]?.name!!
            )
        }
    }
}