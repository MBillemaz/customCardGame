package com.example.customcardgame

import android.graphics.BitmapFactory
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import kotlinx.android.synthetic.main.activity_host_players_role_details.*


class HostPlayersRoleDetails : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_players_role_details)

        var playersRoles = intent.getStringArrayListExtra("PlayersRoles")
        var rolesOrdered = ArrayList<String>(0)

        var db = Room.databaseBuilder(this, CardDatabase::class.java, "cards")
            .build()

        // On ordonne les cartes en alphabétique
        playersRoles.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it })).forEach {

            rolesOrdered.add(it)
        }

        // On crée la liste en mettant l'adaptateur
        var adapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, rolesOrdered)
        listCards.adapter = adapter

        // Récupération de la taille de l'écran
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)


        // Gère le click sur une ligne
        setClickOnItem(db)


        // On cache la partie détails.
        roleDetailsLayout.visibility = View.INVISIBLE
    }

    private fun setClickOnItem(db: CardDatabase) {

        // Lors du click sur une ligne
        listCards.setOnItemClickListener { parent, view, position, id ->

            Thread {
                var cardTitle = listCards.getItemAtPosition(position) as String
                var playerName = cardTitle.split(" - ")[0]
                var cardName = cardTitle.split(" - ")[1]

                var card = db.cardDao().findByName(cardName)
                var cardDescription = card?.description

                if (card?.picture != null) {

                    val decodedString = Base64.decode(card?.picture, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imgCard.setImageBitmap(decodedByte)
                }

                runOnUiThread {
                    txtPlayerName.text = playerName
                    txtCardName.text = cardName
                    txtDescription.text = cardDescription

                    roleDetailsLayout.visibility = View.VISIBLE
                }
            }.start()
        }
    }
}
