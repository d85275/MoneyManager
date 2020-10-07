package com.example.demo.viewmodels

import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo.model.HistoryData
import com.example.demo.utils.AnimHandler
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.view_add_item.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryViewModel : ViewModel() {
    private companion object {
        private const val ANIM_DURATION = 150L
    }

    private val dateFormatForMonth: SimpleDateFormat =
        SimpleDateFormat("MMM - yyyy", Locale.getDefault())
    private val dateFormatForDay: SimpleDateFormat =
        SimpleDateFormat("MM/dd", Locale.getDefault())
    private val dateFormatForAdd: SimpleDateFormat =
        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    val historyData = MutableLiveData<List<HistoryData>>()


    fun getDay(date: Date): String {
        return dateFormatForDay.format(date)
    }

    fun getDate(date: Date): String {
        return dateFormatForMonth.format(date)
    }

    fun getAddDate(): String {
        if (selectedDay.value == null) return ""
        return dateFormatForAdd.format(selectedDay.value!!)
    }

    fun loadData(date: Date?) {
        if (date == null) return
        val list = arrayListOf<HistoryData>()
        for (i in 0..20) {
            val data = HistoryData.create("test $i", "food", 500,"2020-10/03")
            list.add(data)
        }
        historyData.value = list
    }

    val isAddItem = MutableLiveData(false)
    fun addItem(date: Date?) {
        if (date == null) return
        val data = HistoryData.create("test ${historyData.value!!.size}", "food", 500,"2020-10/03")
        val list = (historyData.value as ArrayList)
        list.add(data)
        historyData.value = list
        isAddItem.value = true
    }

    val selectedDay = MutableLiveData<Date>(Calendar.getInstance().time)
    val selectedMonth = MutableLiveData<Date>(Calendar.getInstance().time)
    fun getCalendarListener(): CompactCalendarView.CompactCalendarViewListener {
        return object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
                if (dateClicked == null) return
                selectedDay.value = dateClicked
            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                if (firstDayOfNewMonth == null) return
                selectedMonth.value = firstDayOfNewMonth
                selectedDay.value = firstDayOfNewMonth
            }
        }
    }

    fun getAnimationSetAlpha(): AnimationSet? {
        val animationSet = AnimationSet(true)
        val alphaAnimation = AlphaAnimation(0.1f, 1.0f)
        alphaAnimation.interpolator = DecelerateInterpolator()
        alphaAnimation.duration = ANIM_DURATION
        animationSet.addAnimation(alphaAnimation)
        animationSet.duration = ANIM_DURATION
        return animationSet
    }


}