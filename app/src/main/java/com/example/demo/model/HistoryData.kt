package com.example.demo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val type: String,
    @ColumnInfo val price: Int,
    @ColumnInfo val date: String
) {
    companion object {
        const val TYPE_INCOME = "TYPE_INCOME"
        const val TYPE_EXPENSE = "TYPE_EXPENSE"
        fun create(name: String, type: String, price: Int, date: String): HistoryData {
            return HistoryData(0, name, type, price, date)
        }
    }
}