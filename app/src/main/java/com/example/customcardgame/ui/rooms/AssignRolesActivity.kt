package com.example.customcardgame.ui.rooms

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.customcardgame.Entities.SalutCard
import com.example.customcardgame.HostPlayersRoleDetails
import com.example.customcardgame.R
import com.example.customcardgame.wifi.SingletonNetwork
import kotlinx.android.synthetic.main.activity_assign_roles.*
import kotlinx.android.synthetic.main.activity_player_room.*
import kotlin.random.Random

class AssignRolesActivity : AppCompatActivity() {

    var gameStarted = false

    // Liste des devices que l'on va vider durant les attributions
    var attributionDevice: ArrayList<String> = ArrayList()

    // Liste des cartes disponibles que l'on va vider durant l'attribution
    var attributionCards: ArrayList<SalutCard> = ArrayList()

    // Liste des devices avec leur rôle attribué
    val deviceWithRole: MutableMap<String, SalutCard> = mutableMapOf()

    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.ThemeOverlay_Material_Dark)
        setContentView(R.layout.activity_assign_roles)

        attributionDevice = ArrayList()
        attributionCards = ArrayList()

        // On crée une liste de device qui sera vidée au fur et à mesure
        attributionDevice.addAll(SingletonNetwork.deviceList.keys)
        attributionCards.addAll(SingletonNetwork.allCardsInGame)

        attributionDevice.forEach { device -> deviceWithRole.put(device, SalutCard()) }

        adapter = ArrayAdapter(
            applicationContext,
            R.layout.customizable_list_item,
            deviceWithRole.map { (player, card) ->
                "$player - ${card.cardName}"
            })

        playerList.adapter = adapter


        // Quand on clique sur un utilisateur
        playerList.setOnItemClickListener { _, _, position, _ ->

            var builderSingle = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dark)
            builderSingle.setTitle("Choisir une carte à assigner")

            // Ouvre une modale avec la liste des cartes. Lors du clic sur une des cartes,
            // La modale se ferme et la carte est assignée
            builderSingle.setSingleChoiceItems(
                attributionCards.map { card -> Html.fromHtml("<font color='#FF7F27'>" + card.cardName + "</font>") }.toTypedArray(),
                -1
            ) { dialog, which ->
                val player = adapter.getItem(position)!!.split(" - ")[0]

                val oldCard = deviceWithRole[player]!!
                val card = attributionCards[which]

                // Assigne la carte au joueur, supprime les deux des listes attribuables
                deviceWithRole[player] = card
                attributionCards.removeAt(which)
                attributionDevice.remove(player)

                //Si le joueur avait déjà une carte affectée, on remet la carte dans la liste des cartes
                // a affecter
                if(oldCard.cardName != "") {
                    attributionCards.add(oldCard)
                }

                // mets à jour la liste
                adapter.clear()
                adapter.addAll(deviceWithRole.map { (player, card) -> "$player - ${card.cardName}" })
                adapter.notifyDataSetChanged()

                dialog.dismiss()
            }
            builderSingle.show()
        }

    }

    // Lors du clique sur le bouton pour commencer
    fun onStartClick(view: View) {

        // Pour chaque device qui ne possède pas encore de cartes
        attributionDevice.forEach { device ->

            // On prend une carte au hasard par index et on l'assigne
            val index = Random.nextInt(0, attributionCards.size)
            val card = attributionCards.get(index)

            deviceWithRole.set(device, card)

            attributionCards.removeAt(index)
        }

        attributionDevice.clear()

        // On envoie la carte attribuée à chaque utilisateur
        deviceWithRole.forEach {
            val device = SingletonNetwork.deviceList[it.key]!!
            SingletonNetwork.sendToDevice(device, it.value) {

                try {
                    Log.e(
                        javaClass.simpleName,
                        "Can't send card to device " + it.key
                    )
                } catch (ex: Exception) {

                }
            }
        }

        // Toutes les cartes ont été assignées et envoyées, on navigue vers la page pour montrer à l'admin les rôles assignés
        val intent = Intent(this, HostPlayersRoleDetails::class.java)
        intent.putExtra(
            "PlayersRoles",
            ArrayList(deviceWithRole.map { (player, card) -> "$player - ${card.cardName}" })
        )
        gameStarted = true
        startActivity(intent)
    }

    // Lorsque l'on revient sur cette activité
    override fun onResume() {
        super.onResume()

        // Si on a déjà créé un salon on revient directement sur l'écran précédent
        if (gameStarted) {
            onBackPressed()
        }
    }
}
