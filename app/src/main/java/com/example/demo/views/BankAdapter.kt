package com.example.demo.views

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import kotlinx.android.synthetic.main.item_bank_card_add.view.*
import kotlinx.android.synthetic.main.item_history.view.tvName

class BankAdapter : RecyclerView.Adapter<BankAdapter.BaseBankViewHolder>() {
    private var alNames = arrayListOf<String?>(null)

    private companion object {
        private const val EMPTY = 0
        private const val BANK = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBankViewHolder {
        return when (viewType) {
            EMPTY -> {
                EmptyViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bank_card_add, parent, false)
                )
            }
            else -> {
                BankViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_bank_card, parent, false)
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

    fun setList(list: List<String?>) {
        alNames = list as ArrayList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (alNames[position] == null) EMPTY else BANK
    }

    open class BaseBankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bindView(position: Int, list: List<String?>) {}
    }

    class BankViewHolder(itemView: View) : BaseBankViewHolder(itemView) {
        override fun bindView(position: Int, list: List<String?>) {
            itemView.tvName.text = list[position]
        }
    }

    class EmptyViewHolder(itemView: View) : BaseBankViewHolder(itemView) {
        override fun bindView(position: Int, list: List<String?>) {
            itemView.ivAddBank.setOnClickListener {
                showDialog()
            }
        }

        private fun showDialog() {
            val dialog = Dialog(itemView.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.view_add_bank)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.findViewById<Button>(R.id.btConfirm).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<Button>(R.id.btCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}