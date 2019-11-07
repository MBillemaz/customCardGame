package com.example.customcardgame.hostData

class HostCardsdata(val name: String) {

    private var numberOfCards: Int = 0
    private var cardName: String = name

    fun getNumberOfCards(): Int {
        return numberOfCards
    }

    fun setNumberOfCards(number: Int) {
        this.numberOfCards = number
    }

    fun getCardName(): String {
        return cardName
    }

    fun setCardName(name: String) {
        this.cardName = name
    }
}
