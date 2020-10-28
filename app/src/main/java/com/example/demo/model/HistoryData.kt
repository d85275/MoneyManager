package com.example.demo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo var name: String,
    @ColumnInfo var type: String, // income/expense
    @ColumnInfo var price: Double,
    @ColumnInfo var date: String,
    @ColumnInfo var source: String, // cash or bank name
    @ColumnInfo var iconPosition: Int
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
            iconPosition: Int
        ): HistoryData {
            return HistoryData(0, name, type, price, date, source, iconPosition)
        }
    }

    fun set(
        name: String,
        type: String,
        price: Double,
        date: String,
        source: String,
        iconPosition: Int
    ) {
        this.name = name
        this.type = type
        this.price = price
        this.date = date
        this.source = source
        this.iconPosition = iconPosition
    }
}