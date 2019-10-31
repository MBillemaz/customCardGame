package com.example.customcardgame.Entities

import android.net.Uri
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class Card (

    @PrimaryKey
    @NonNull
    var cardName: String = "",

    @ColumnInfo(name = "description")
    @Ignore
    var description: String = "",

    @ColumnInfo(name = "image")
    @Ignore
    var picture: String? = null
)
