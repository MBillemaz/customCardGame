package com.example.customcardgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_host_players_role_details.*

class HostPlayersRoleDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_players_role_details)

        var playersRoles = intent.getStringArrayListExtra("PlayersRoles")
        var rolesOrdered = ArrayList<String>(0)

        // On ordonne les cartes en alphabétique
        playersRoles.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it })).forEach {

            rolesOrdered.add(it)
        }

        // On crée la liste en mettant l'adaptateur
        var adapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, rolesOrdered)
        listCards.adapter = adapter


        // Lors du click sur une carte
        listCards.setOnItemClickListener { parent, view, position, id ->

            var builderSingle = AlertDialog.Builder(this)
            var playerName = listCards.getItemAtPosition(position) as String
            playerName = playerName.split(" - ")[0]

            builderSingle.setTitle("Détails de la carte : de '" + playerName + "'")

            builderSingle.show()
        }
    }
}
