package com.example.demo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.demo.model.BankData
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface BankDao {
    @Query("SELECT * FROM bankdata")
    fun getAll(): Single<List<BankData?>>

    @Query("SELECT * FROM bankdata WHERE name IN (:bankNames)")
    fun loadAllByNames(bankNames: List<String>): List<BankData>

    @Insert
    fun insertAll(vararg bankData: BankData): Completable

    @Delete
    fun delete(bankData: BankData): Completable
}