package com.example.customcardgame.Entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card (

    @PrimaryKey
    @NonNull
    var cardName: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "picture")
    var picture: String? = null
)
