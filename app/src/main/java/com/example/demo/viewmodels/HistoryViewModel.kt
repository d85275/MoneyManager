package com.example.demo.viewmodels

import android.graphics.Color
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo.Repository
import com.example.demo.model.HistoryData
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.time.temporal.TemporalQueries.localTime
import java.util.*


class HistoryViewModel(private val repository: Repository) : ViewModel() {
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

    fun getDataByDay(date: Date): List<HistoryData> {
        if (historyData.value == null) return arrayListOf()
        val curDate = dateFormatForAdd.format(date)
        Log.e("123", "curDate: $curDate")
        return historyData.value!!.filter { it.date == curDate }
    }

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
        /*
        val list = arrayListOf<HistoryData>()
        for (i in 0..20) {
            val data = HistoryData.create("test $i", "food", 500,"2020-10/03")
            list.add(data)
        }
        historyData.value = list
         */
    }

    fun loadHistoryData() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(
            repository.getHistoryData().subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).doOnError { e -> Log.e("PP", "Error when getting saved records: $e") }
                .subscribe { list ->
                    list.reversed()
                    historyData.postValue(list)
                }
        )
    }

    fun getEvents(alData: List<HistoryData>): List<Event> {
        val list = arrayListOf<Event>()
        Log.e("123","size: ${alData.size}")
        for (i in alData.indices) {
            //Log.e("123","i: $i")
            val timestamp = dateFormatForAdd.parse(alData[i].date)
            Log.e("13","timestamp: ${timestamp.time}")
            list.add(Event(Color.GRAY, timestamp.time))
        }
        return list
    }

    val isAddItem = MutableLiveData(false)
    fun addItem(date: Date?) {
        if (date == null) return
/*
val data = HistoryData.create("test ${historyData.value!!.size}", "food", 500,"2020-10/03")
val list = (historyData.value as ArrayList)
list.add(data)
historyData.value = list
isAddItem.value = true

 */
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