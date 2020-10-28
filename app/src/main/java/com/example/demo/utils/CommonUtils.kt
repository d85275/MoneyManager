package com.example.demo.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.BankData
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.views.main.bank_frag.BankCardColorAdapter
import com.example.demo.views.history.HistoryActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object CommonUtils {

    fun e(msg: String) {
        Log.e("Debug", msg)
    }

    fun addItemDate(): SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd - EEE", Locale.getDefault())
    fun getPriceFormat(): NumberFormat = DecimalFormat("#,###.##")
    fun getEventDateFormat(): SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    fun getCalendarDateFormat(): SimpleDateFormat =
        SimpleDateFormat("MMM - yyyy", Locale.getDefault())

    fun goHistory(context: Context) {
        val intent = Intent()
        val bundle = Bundle()
        intent.setClass(context, HistoryActivity::class.java)
        intent.putExtras(bundle)
        context.startActivity(intent)
    }

    fun showDialog(context: Context, title: String, msg: String) {
        showDialog(context, title, msg, null, null)
    }

    fun showDialog(
        context: Context,
        title: String,
        msg: String,
        confirmAction: (() -> Unit)?,
        cancelAction: (() -> Unit)?
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.view_common_dialog)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TextView>(R.id.tvTitle).text = title
        dialog.findViewById<TextView>(R.id.tvMsg).text = msg

        dialog.findViewById<Button>(R.id.btConfirm).setOnClickListener {
            confirmAction?.invoke()
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.btCancel).setOnClickListener {
            cancelAction?.invoke()
            dialog.dismiss()
        }
        dialog.show()
    }


    fun showEditBankDialog(
        itemView: View,
        mainViewModel: MainViewModel,
        bankData: BankData?,
        oldName: String
    ) {
        val dialog = Dialog(itemView.context)
        val adapter = BankCardColorAdapter(
            mainViewModel.getBankColor()
        )
        adapter.setSelectedIdx(mainViewModel.getBankColorPosition(bankData?.color))
        initView(dialog, adapter, itemView)
        dialog.findViewById<TextView>(R.id.tvTitle).text = itemView.context.getText(R.string.edit)
        dialog.findViewById<EditText>(R.id.etName).setText(bankData?.name)
        val rvColor = dialog.findViewById<RecyclerView>(R.id.rvColours)
        rvColor.smoothScrollToPosition(mainViewModel.getBankColorPosition(bankData?.color))
        val ivDelete = dialog.findViewById<ImageView>(R.id.ivDelete)
        ivDelete.visibility = View.VISIBLE
        ivDelete.setOnClickListener {
            showRemoveBankDialog(dialog, bankData, itemView, mainViewModel)
        }
        dialog.findViewById<Button>(R.id.btConfirm).setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.etName).text.toString().trim()
            if (name.isEmpty()) {
                return@setOnClickListener
            }
            if (bankData != null) {
                bankData.name = name
                bankData.color = adapter.getSelectedColor()
                mainViewModel.updateBank(bankData, oldName)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRemoveBankDialog(
        dialog: Dialog,
        bankData: BankData?,
        itemView: View,
        mainViewModel: MainViewModel
    ) {
        //val title = itemView.context.getString(R.string.remove_bank)
        val name = bankData?.name?.trim()
        val title = itemView.context.getString(R.string.remove_bank_title, name)
        val msg = itemView.context.getString(R.string.remove_bank_msg)
        showDialog(
            itemView.context, title, msg,
            {
                if (bankData != null) {
                    mainViewModel.removeBank(bankData)
                }
                dialog.dismiss()
            },
            null
        )
    }

    fun showAddBankDialog(itemView: View, mainViewModel: MainViewModel) {
        val dialog = Dialog(itemView.context)
        val adapter = BankCardColorAdapter(
            mainViewModel.getBankColor()
        )
        initView(dialog, adapter, itemView)
        dialog.findViewById<Button>(R.id.btConfirm).setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.etName).text.toString().trim()
            if (name.isEmpty()) {
                return@setOnClickListener
            }
            mainViewModel.addBank(BankData.create(name, adapter.getSelectedColor()))
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initView(dialog: Dialog, adapter: BankCardColorAdapter, itemView: View) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.view_add_bank)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<Button>(R.id.btCancel).setOnClickListener {
            dialog.dismiss()
        }
        val rvColor = dialog.findViewById<RecyclerView>(R.id.rvColours)
        rvColor.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        rvColor.setHasFixedSize(true)
        rvColor.adapter = adapter
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}