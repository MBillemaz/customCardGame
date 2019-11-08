package com.example.customcardgame.hostData

class HostCardsdata(val name: String) {

    private var numberOfCards: Int = 0
    private var cardName: String = name

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
        return cardName
    }

    fun setCardName(name: String) {
        this.cardName = name
    }
}
