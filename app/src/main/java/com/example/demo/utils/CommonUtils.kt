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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.R
import com.example.demo.model.BankData
import com.example.demo.viewmodels.MainViewModel
import com.example.demo.views.BankCardColorAdapter
import com.example.demo.views.HistoryActivity
import kotlin.math.abs

object CommonUtils {

    fun getGesture(activity: Activity, action: () -> Unit, isEnter: Boolean): GestureDetector {
        return GestureDetector(
            activity,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    Log.i("Bank", "onFling has been called!")
                    val SWIPE_MIN_DISTANCE = 120
                    val SWIPE_MAX_OFF_PATH = 250
                    val SWIPE_THRESHOLD_VELOCITY = 200
                    try {
                        if (abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) return false
                        if (e1.x - e2.x > SWIPE_MIN_DISTANCE
                            && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                        ) {
                            Log.e("SWIPE", "Right to Left")
                            if (isEnter) action.invoke()
                        } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE
                            && abs(velocityX) > SWIPE_THRESHOLD_VELOCITY
                        ) {
                            if (!isEnter) action.invoke()
                            Log.e("SWIPE", "Left to Right")
                        }
                    } catch (e: Exception) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY)
                }
            })
    }

    const val FROM_CASH = 0
    const val FROM_BANK = 1
    const val KEY_FROM = "KEY_FROM"
    fun goHistory(context: Context, from: Int) {
        val intent = Intent()
        val bundle = Bundle()
        intent.setClass(context, HistoryActivity::class.java)
        bundle.putInt(KEY_FROM, from)
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


    fun showEditBankDialog(itemView: View, mainViewModel: MainViewModel, bankData: BankData?) {
        val dialog = Dialog(itemView.context)
        val adapter = BankCardColorAdapter(mainViewModel.getBankColor())
        adapter.setSelectedIdx(mainViewModel.getBankColorPosition(bankData?.color))
        initView(dialog, adapter, itemView)
        dialog.findViewById<TextView>(R.id.tvTitle).text = itemView.context.getText(R.string.edit)
        dialog.findViewById<EditText>(R.id.etName).setText(bankData?.name)
        val rvColor = dialog.findViewById<RecyclerView>(R.id.rvColours)
        rvColor.smoothScrollToPosition(mainViewModel.getBankColorPosition(bankData?.color))
        dialog.findViewById<Button>(R.id.btConfirm).setOnClickListener {
            val name = dialog.findViewById<EditText>(R.id.etName).text.toString().trim()
            if (name.isEmpty()) {
                return@setOnClickListener
            }
            mainViewModel.updateBank(BankData.create(name, adapter.getSelectedColor()))
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showAddBankDialog(itemView: View, mainViewModel: MainViewModel) {
        val dialog = Dialog(itemView.context)
        val adapter = BankCardColorAdapter(mainViewModel.getBankColor())
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