package com.example.demo

import android.content.Context
import androidx.room.Room
import com.example.demo.database.AppDatabase
import com.example.demo.model.BankData
import com.example.demo.model.HistoryData
import io.reactivex.Completable
import io.reactivex.Single

class Repository(context: Context) {
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "database-name").build()

    fun getBank(): Single<List<BankData?>> {
        return db.bankDao().getAll()
    }

    fun addBank(bankData: BankData): Completable {
        return db.bankDao().insertAll(bankData)
    }

    fun removeBank(bankData: BankData): Completable {
        return db.bankDao().delete(bankData)
    }

    //fun getRecentCashData(): Single<List<HistoryData>> {
    //    return db.historyDao().loadCashRecent()
    //}

    fun getHistoryData():Single<List<HistoryData>>{
        return db.historyDao().getAll()
    }

    fun getRecentHistoryData(source: String): Single<List<HistoryData>> {
        return db.historyDao().loadRecentData(source)
    }

    fun addHistory(historyData: HistoryData): Completable {
        return db.historyDao().insertAll(historyData)
    }
}