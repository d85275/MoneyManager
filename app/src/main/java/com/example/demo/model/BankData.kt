package com.example.demo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BankData(@PrimaryKey val name: String, @ColumnInfo val color: Int) {
    companion object {
        const val COLOR_GREEN = 0
        const val COLOR_BLUE = 1
        const val COLOR_BLUE_2 = 2
        const val COLOR_PURPLE = 3
        const val COLOR_RED = 4
        fun create(name: String) = BankData(name, COLOR_GREEN)
        fun create(name: String, color: Int) = BankData(name, color)
    }
}