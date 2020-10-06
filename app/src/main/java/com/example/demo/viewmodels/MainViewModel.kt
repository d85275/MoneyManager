package com.example.demo.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2

class MainViewModel : ViewModel() {
    // fragments
    private var desId = -1


    fun setDesId(id: Int) {
        desId = id
    }

    val curPage = MutableLiveData(0)
    fun getChangeCallback() = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            curPage.value = position
        }
    }
}