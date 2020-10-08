package com.example.demo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val type: String, // income/expense
    @ColumnInfo val price: Double,
    @ColumnInfo val date: String,
    @ColumnInfo val source: String, // cash or bank name
    @ColumnInfo val icon: Int
) {
    companion object {
        const val TYPE_INCOME = "INCOME"
        const val TYPE_EXPENSE = "EXPENSE"
        const val SOURCE_CASH = "CASH"
        fun create(
            name: String,
            type: String,
            price: Double,
            date: String,
            source: String,
            icon: Int
        ): HistoryData {
            return HistoryData(0, name, type, price, date, source, icon)
        }
    }
}