package com.example.demo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BankData(@PrimaryKey val name: String)