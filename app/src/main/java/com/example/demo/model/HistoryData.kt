package com.example.demo.model

data class HistoryData(val name: String, val type: String, val price: Int, val date:String) {
    companion object{
        const val TYPE_INCOME = "TYPE_INCOME"
        const val TYPE_EXPENSE = "TYPE_EXPENSE"
    }
}