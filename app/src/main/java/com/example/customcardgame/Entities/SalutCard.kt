package com.example.customcardgame.Entities

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class SalutCard (

    @JsonField
    var cardName: String = "",

    @JsonField
    var description: String = "",

    @JsonField
    var picture: String = ""
)
