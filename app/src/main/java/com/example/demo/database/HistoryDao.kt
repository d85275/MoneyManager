package com.example.demo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.demo.model.BankData
import com.example.demo.model.HistoryData
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface HistoryDao {
    @Query("SELECT * FROM historydata")
    fun getAll(): Single<List<HistoryData>>

    @Query("SELECT * FROM historydata WHERE source IN (:source)")
    fun loadAllBySource(source: String): Single<List<HistoryData>>

    @Query("SELECT * FROM historydata WHERE type IN (:type)")
    fun loadAllByType(type: String): Single<List<HistoryData>>

    //@Query("SELECT * FROM historydata LIMIT 4")
    //fun loadCashRecent(): Single<List<HistoryData>>

    @Query("SELECT * FROM historydata WHERE source IN (:source) LIMIT 4")
    fun loadRecentData(source: String): Single<List<HistoryData>>

    @Insert
    fun insertAll(vararg historyData: HistoryData): Completable

    @Delete
    fun delete(historyData: HistoryData): Completable
}