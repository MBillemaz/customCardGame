package com.example.customcardgame.ui.cardDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.Entities.Card
import com.example.customcardgame.R
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetails : AppCompatActivity() {

    var oldName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)


        // On récupère et on affiche le nom de la carte
        oldName = intent.getStringExtra("cardName")!!
        edtCardName.setText(oldName)


        // Quand on clique sur le bouton pour annuler on ne valide pas les modifs
        btnCancel.setOnClickListener {

            moveTaskToBack(true)
        }

        // Quand on clique sur le bouton pour valider on valide les modifs
        btnValid.setOnClickListener {

            saveCard()
            moveTaskToBack(true)
        }
    }


    // Enregsitre une carte dans la BDD
    private fun saveCard() {

        val db = Room.databaseBuilder(applicationContext, CardDatabase::class.java, "cards")
            .allowMainThreadQueries()
            .build()

        var newName = edtCardName.text.toString()
        var description = edtDescription.text.toString()

        var thisCard: Card = db.cardDao().findByName(oldName)

        // Si le nom a été changé on vérifie qu'une autre carte n'a pas le même nouveau nom
        if (oldName != newName) {

            // Si le nouveau nom n'existe pas encore on l'ajoute
            if (db.cardDao().findByName("zertzert") == null) {

            }
            // Si le nouveau nom existe déjà on affiche une erreur
            else {

            }
        }
    }
}
