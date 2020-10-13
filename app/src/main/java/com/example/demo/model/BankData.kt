package com.example.demo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.demo.R

@Entity
data class BankData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo var name: String,
    @ColumnInfo var color: Int
) {
    companion object {
        fun create(name: String) = BankData(0, name, R.drawable.icon_bank_green)
        fun create(name: String, color: Int) = BankData(0, name, color)
    }
}