package com.example.demo.viewmodels

import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    // fragments
    private var desId = -1


    fun setDesId(id: Int) {
        desId = id
    }
}