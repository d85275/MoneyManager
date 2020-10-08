package com.example.demo.views

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.BankData
import com.example.demo.utils.CommonUtils
import com.example.demo.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.item_bank_card.view.*
import kotlinx.android.synthetic.main.item_bank_card_add.view.*

class BankAdapter : RecyclerView.Adapter<BankAdapter.BaseBankViewHolder>() {
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

    open class BaseBankViewHolder(itemView: View, val mainViewModel: MainViewModel) :
        RecyclerView.ViewHolder(itemView) {
        open fun bindView(position: Int, list: List<BankData?>) {}
    }

    class BankViewHolder(itemView: View, mainViewModel: MainViewModel) :
        BaseBankViewHolder(itemView, mainViewModel) {
        override fun bindView(position: Int, list: List<BankData?>) {
            itemView.clBackground.setBackgroundResource(
                list[position]?.color ?: R.drawable.icon_bank_green
            )
            itemView.tvName.text = list[position]?.name
            itemView.ivRemove.setOnClickListener {
                showRemoveBankDialog()
            }
        }

        private fun showRemoveBankDialog() {
            val title = itemView.context.getString(R.string.remove_bank)
            val name = itemView.tvName.text.toString().trim()
            val msg = itemView.context.getString(R.string.remove_bank_msg, name)
            CommonUtils.showDialog(
                itemView.context,
                title,
                msg,
                { mainViewModel.removeBank(BankData.create(name)) },
                null
            )
        }
    }

    class EmptyViewHolder(itemView: View, mainViewModel: MainViewModel) :
        BaseBankViewHolder(itemView, mainViewModel) {

        override fun bindView(position: Int, list: List<BankData?>) {
            itemView.ivAddBank.setOnClickListener {
                CommonUtils.showAddBankDialog(itemView, mainViewModel)
            }
        }
    }
}