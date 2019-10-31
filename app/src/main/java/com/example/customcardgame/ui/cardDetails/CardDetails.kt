package com.example.customcardgame.ui.cardDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.customcardgame.R
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)


        // On récupère le nom de la carte
        var cardName = intent.getStringExtra("cardName")

        edtCardName.setText(cardName)
    }
}
