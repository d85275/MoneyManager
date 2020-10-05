package com.example.demo.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo.model.HistoryData
import java.util.*

class CashViewModel:ViewModel() {
    val recentData = MutableLiveData<List<HistoryData>>()
    fun loadRecentData() {
        val list = arrayListOf<HistoryData>()
        for (i in 0..2) {
            val data = HistoryData("海鮮麵", "food", 190,"2020-10/03")
            list.add(data)
        }
        recentData.value = list
    }
}