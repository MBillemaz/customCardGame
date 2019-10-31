package com.example.customcardgame.ui.cards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CardsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Cards Fragment"
    }
    val text: LiveData<String> = _text
}