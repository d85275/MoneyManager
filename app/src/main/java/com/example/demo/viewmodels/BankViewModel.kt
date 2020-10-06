package com.example.demo.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2
import com.example.demo.model.HistoryData
import com.example.demo.model.HistoryData.Companion.TYPE_EXPENSE
import com.example.demo.model.HistoryData.Companion.TYPE_INCOME
import java.util.*
import kotlin.math.abs
import kotlin.math.max

class BankViewModel : ViewModel() {
    val recentData = MutableLiveData<List<HistoryData>>()
    val bankData = MutableLiveData<List<String?>>()
    fun loadRecentData() {
        val list = arrayListOf<HistoryData>()
        val data1 = HistoryData("薪水", TYPE_INCOME, 32000, "2020-10/05")
        list.add(data1)
        val data2 = HistoryData("提款", TYPE_EXPENSE, 2000, "2020-09/30")
        list.add(data2)
        val data3 = HistoryData("ＶＳ簽帳消費", TYPE_EXPENSE, 499, "2020-09/23")
        list.add(data3)
        val data4 = HistoryData("提款", TYPE_EXPENSE, 1000, "2020-09/30")
        list.add(data4)
        recentData.value = list
    }

    fun loadBankData() {
        val list = arrayListOf<String?>()
        val data1 = "台灣銀行"
        list.add(data1)
        val data2 = "玉山銀行"
        list.add(data2)
        val data3 = "元大銀行"
        list.add(data3)
        val data4 = null
        list.add(data4)
        bankData.value = list
    }

    fun getTransformer() = ViewPager2.PageTransformer { page, position ->
        val myOffset: Float = position * -(2 * 30)
        when {
            position < -1 -> {
                page.translationY = -myOffset
            }
            position <= 1 -> {
                val scaleFactor =
                    max(0.7f, 1 - abs(position - 0.14285715f))
                page.translationY = myOffset
                //page.alpha = scaleFactor
            }
            else -> {
                page.translationY = myOffset
                //page.alpha = 0f
            }
        }
    }
}