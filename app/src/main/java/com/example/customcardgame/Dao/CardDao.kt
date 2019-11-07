package com.example.customcardgame.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.customcardgame.Entities.Card

@Dao
interface CardDao {

    @Query("SELECT cardName FROM cards")
    fun getAllNames(): Array<String>

    @Query("SELECT * FROM cards WHERE cardName LIKE :name LIMIT 1")
    fun findByName(name: String): Card?

    @Insert
    fun insertAll(vararg cards: Card)

    @Delete
    fun delete(card: Card)

    @Query("DELETE FROM cards WHERE cardName = :name")
    fun deleteByName(name: String)
}

