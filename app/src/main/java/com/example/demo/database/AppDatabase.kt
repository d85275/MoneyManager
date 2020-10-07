package com.example.demo.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.demo.model.BankData

@Database(entities = [BankData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankDao(): BankDao
    abstract fun userDao(): UserDao
}