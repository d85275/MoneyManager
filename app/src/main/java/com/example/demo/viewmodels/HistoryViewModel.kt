package com.example.demo.viewmodels

import android.graphics.Color
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.demo.Repository
import com.example.demo.model.HistoryData
import com.example.demo.utils.CommonUtils
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import java.text.SimpleDateFormat
import java.util.*


class HistoryViewModel() : ViewModel() {
    private companion object {
        private const val ANIM_DURATION = 250L
    }

    val isEditMode = MutableLiveData(false)
    val selectedDay = MutableLiveData<Date>(Calendar.getInstance().time)
    val selectedMonth = MutableLiveData<Date>(Calendar.getInstance().time)

    fun setEditMode(isActivate: Boolean) {
        isEditMode.value = isActivate
    }

    fun getDate(date: Date): String {
        return CommonUtils.getCalendarDateFormat().format(date)
    }

    fun getEvents(alData: List<HistoryData>): List<Event> {
        val list = arrayListOf<Event>()
        for (i in alData.indices) {
            val timestamp = CommonUtils.getEventDateFormat().parse(alData[i].date)
            list.add(Event(Color.GRAY, timestamp.time))
        }
        return list
    }

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
        val alphaAnimation = AlphaAnimation(0f, 1.0f)
        alphaAnimation.interpolator = DecelerateInterpolator()
        alphaAnimation.duration = ANIM_DURATION
        animationSet.addAnimation(alphaAnimation)
        animationSet.duration = ANIM_DURATION
        return animationSet
    }
}