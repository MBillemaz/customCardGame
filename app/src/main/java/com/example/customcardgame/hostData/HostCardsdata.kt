package com.example.customcardgame.hostData

import com.example.customcardgame.Entities.Card

class HostCardsdata(val card: Card) {

    private var numberOfCards: Int = 0

    fun getNumberOfCards(): Int {
        return numberOfCards
    }

    fun increaseNumberOfCards(): Boolean {
        numberOfCards++
        return true
    }

    fun decreaseNumberOfCards(): Boolean {
        if(numberOfCards > 0) {
            numberOfCards--
            return true
        }
        return false
    }

    fun getCardName(): String {
        return card.cardName
    }
}
