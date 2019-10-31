package com.example.customcardgame.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.customcardgame.Dao.CardDao
import com.example.customcardgame.Entities.Card

@Database(entities = arrayOf(Card::class), version = 1)
abstract class CardDatabase: RoomDatabase() {

    abstract fun cardDao(): CardDao
}

