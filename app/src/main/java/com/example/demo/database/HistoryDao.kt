package com.example.demo.database

import androidx.room.*
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

    @Query("SELECT * FROM historydata WHERE source IN (:source) LIMIT 4")
    fun loadRecentData(source: String): Single<List<HistoryData>>

    @Insert
    fun insertAll(vararg historyData: HistoryData): Completable

    @Delete
    fun delete(historyData: HistoryData): Completable

    @Query("DELETE FROM historydata WHERE source IN (:source)")
    fun deleteFromSource(source: String): Completable

    @Query("DELETE FROM historydata WHERE id IN (:ids)")
    fun deleteFromIds(ids: List<Int>): Completable

    @Query("UPDATE historydata SET source = :newName WHERE source=:oldName")
    fun updateSourceName(newName: String, oldName: String): Completable

    @Update
    fun updateHistory(historyData: HistoryData): Completable
}