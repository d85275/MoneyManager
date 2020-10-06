package com.example.demo.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo.model.HistoryData
import java.util.*

class CashViewModel : ViewModel() {
    val recentData = MutableLiveData<List<HistoryData>>()
    fun loadRecentData() {
        val list = arrayListOf<HistoryData>()
        val data1 = HistoryData("薪水", HistoryData.TYPE_INCOME, 32000, "2020-10/05")
        list.add(data1)
        val data2 = HistoryData("海鮮麵", HistoryData.TYPE_EXPENSE, 190, "2020-09/30")
        list.add(data2)
        val data3 = HistoryData("ＶＳ簽帳消費", HistoryData.TYPE_EXPENSE, 499, "2020-09/23")
        list.add(data3)
        val data4 = HistoryData("瓦斯費", HistoryData.TYPE_EXPENSE, 650, "2020-09/30")
        list.add(data4)
        recentData.value = list
    }
}