package com.example.customcardgame


import android.graphics.BitmapFactory
import android.graphics.Point
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_host_players_role_details.*


class HostPlayersRoleDetails : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.ThemeOverlay_Material_Dark)
        setContentView(R.layout.activity_host_players_role_details)

        var playersRoles = intent.getStringArrayListExtra("PlayersRoles")
        var rolesOrdered = ArrayList<String>(0)

        var db = Room.databaseBuilder(this, CardDatabase::class.java, "cards")
            .build()

        // On ordonne les cartes en alphabétique
        playersRoles?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it }))?.forEach {

            rolesOrdered.add(it)
        }

        // On crée la liste en mettant l'adaptateur
        var adapter =
            ArrayAdapter(applicationContext, R.layout.customizable_list_item, rolesOrdered)
        listCards.adapter = adapter

        // Récupération de la taille de l'écran
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)


        // Gère le clique sur une ligne
        setClickOnItem(db)

        // Gère le clique sur le bouton d'annulation
        setOnClickBtnCancel()

        // On cache la partie détails.
        roleDetailsLayout.visibility = View.INVISIBLE
    }

    // Lors du clique sur le nom d'un joueur
    private fun setClickOnItem(db: CardDatabase) {

        // Lors du click sur une ligne
        listCards.setOnItemClickListener { _, _, position, _ ->

            Thread {

                // On affiche le nom du joueur, le nom de la carte, sa description, et son image
                var cardTitle = listCards.getItemAtPosition(position) as String
                var playerName = cardTitle.split(" - ")[0]
                var cardName = cardTitle.split(" - ")[1]

                var card = db.cardDao().findByName(cardName)
                var cardDescription = card?.description

                if (card?.picture != null) {

                    val decodedString = Base64.decode(card.picture, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imgCard.setImageBitmap(decodedByte)
                }


                runOnUiThread {
                    txtPlayerName.text = playerName
                    txtCardName.text = cardName
                    txtDescription.text = cardDescription

                    // On cache l'écran précédent
                    roleDetailsLayout.visibility = View.VISIBLE
                    listCards.visibility = View.INVISIBLE
                }
            }.start()
        }
    }

    // Lors du clique sur le bouton d'annulation
    private fun setOnClickBtnCancel() {

        btnCancel.setOnClickListener {

            roleDetailsLayout.visibility = View.INVISIBLE
            listCards.visibility = View.VISIBLE
        }
    }

    // Lorsque l'on clique sur le bouton pour revenir à l'écran précédent
    override fun onBackPressed() {

        //Si on est dans les détails d'un joueur on n'autorise pas tout de suite à revenir sur l'écran précédent
        if (roleDetailsLayout.visibility == View.VISIBLE) {

            roleDetailsLayout.visibility = View.INVISIBLE
            listCards.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}
