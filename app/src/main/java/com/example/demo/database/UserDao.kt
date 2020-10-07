package com.example.demo.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.demo.model.BankData

@Dao
interface UserDao {
    @Query("SELECT * FROM bankdata")
    fun getAll(): List<BankData>

    @Query("SELECT * FROM bankdata WHERE name IN (:bankNames)")
    fun loadAllByNames(bankNames: List<String>): List<BankData>

    @Insert
    fun insertAll(vararg bankData: BankData)

    @Delete
    fun delete(bankData: BankData)
}