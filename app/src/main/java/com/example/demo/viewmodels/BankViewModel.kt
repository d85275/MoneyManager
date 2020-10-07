package com.example.demo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.viewpager2.widget.ViewPager2

class BankViewModel : ViewModel() {
    fun getTransformer() = ViewPager2.PageTransformer { page, position ->
        val myOffset: Float = position * -(2 * 30)
        when {
            position < -1 -> {
                page.translationY = -myOffset
            }
            position <= 1 -> {
                //val scaleFactor = max(0.7f, 1 - abs(position - 0.14285715f))
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