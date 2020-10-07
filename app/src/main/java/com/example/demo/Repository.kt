package com.example.demo

import android.content.Context
import androidx.room.Room
import com.example.demo.database.AppDatabase
import com.example.demo.model.BankData
import io.reactivex.Completable
import io.reactivex.Single

class Repository(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()

    fun getBank(): Single<List<BankData?>> {
        return db.bankDao().getAll()
    }

    fun addBank(bankData: BankData):Completable {
        return db.bankDao().insertAll(bankData)
    }
}