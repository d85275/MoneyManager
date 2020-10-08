package com.example.demo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.demo.R

@Entity
data class BankData(@PrimaryKey val name: String, @ColumnInfo val color: Int) {
    companion object {
        fun create(name: String) = BankData(name, R.drawable.icon_bank_green)
        fun create(name: String, color: Int) = BankData(name, color)
    }
}