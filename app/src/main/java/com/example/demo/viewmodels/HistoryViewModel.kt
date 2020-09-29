package com.example.demo.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.activity_history.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewModel: ViewModel() {
    private val dateFormatForMonth: SimpleDateFormat =
        SimpleDateFormat("MMM - yyyy", Locale.getDefault())
    private val dateFormatForDay: SimpleDateFormat =
        SimpleDateFormat("MM/dd", Locale.getDefault())

    fun getDay(date:Date):String{
        return dateFormatForDay.format(date)
    }

    fun getDate(date: Date):String{
        return  dateFormatForMonth.format(date)
    }


    val selectedDay = MutableLiveData<Date>()
    val selectedMonth = MutableLiveData<Date>()
    fun getCalendarListener():CompactCalendarView.CompactCalendarViewListener {
        return object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                if (dateClicked==null) return
                selectedDay.value = dateClicked
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                if (firstDayOfNewMonth == null) return
                selectedMonth.value = firstDayOfNewMonth
            }
        }
    }
}