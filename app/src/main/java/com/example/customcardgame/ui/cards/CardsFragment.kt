package com.example.customcardgame.ui.cards

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.R
import com.example.customcardgame.ui.cardDetails.CardDetails
import kotlinx.android.synthetic.main.fragment_cards.*
import kotlinx.android.synthetic.main.fragment_cards.view.*

class CardsFragment : Fragment() {

//    private lateinit var cardsViewModel: CardsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_cards, container, false)


        // Lors du click pour ajouter une carte
        root.fabAddCard.setOnClickListener {

            // On ajoute la carte
            onClickAddCard(context!!/*, listCards, listItems*/)
        }

        // Au click sur une carte
        root.listCards.setOnItemClickListener { parent, view, position, id ->

            // On charge les détails
            openCardDetails(context!!, ((view as TextView).text) as String)
        }

        // On reste appuyé pour supprimer les cartes
        root.listCards.setOnItemLongClickListener { parent, view, position, id ->

            AlertDialog.Builder(context)

                // Affichage du message de confirmation de suppressino
                .setTitle("Suppression")
                .setMessage("Voulez vous supprimer cette carte ?")
                .setPositiveButton(
                    "Supprimer"
                ) { dialog, _ ->

                    // On a cliqué sur le bouton pour confirmer la suppression
                    val db = Room.databaseBuilder(
                        context!!.applicationContext,
                        CardDatabase::class.java,
                        "cards"
                    )
                        .allowMainThreadQueries()
                        .build()

                    db.cardDao().deleteByName(((view as TextView).text) as String)
                    loadAllCards(context!!, listCards)
                    dialog.dismiss()
                }

                // Bouton pour annuler la suppresion
                .setNegativeButton("Annuler")
                { dialog, which ->
                    dialog.dismiss()
                }
                .create().show()

            true
        }

        return root
    }


    // Quand la page apparait ou réapparait
    override fun onResume() {
        super.onResume()

        // On rafraichit la liste des cartes
        loadAllCards(context!!, listCards)
    }
}


// Ajoute une carte dans la liste
private fun onClickAddCard(context: Context/*, listCards: ListView, listItems: ArrayList<String>*/) {

    openCardDetails(context, "")
}


// Ouvre la page des détails d'une carte selon son nom
private fun openCardDetails(context: Context, cardName: String) {

    var intent = Intent(context, CardDetails::class.java)
    intent.putExtra("cardName", cardName)
    context.startActivity(intent)
}

// Charge les cartes enregistrées
private fun loadAllCards(context: Context, listCardsName: ListView) {

    // Récupère la database
    val db = Room.databaseBuilder(context, CardDatabase::class.java, "cards")
        .allowMainThreadQueries()
        .build()

    // Récupère tous les noms des cartes
    var allCardsName = db.cardDao().getAllNames()
    var listNames = ArrayList<String>(0)

    // Pour chaque nom on l'enregistre dans l'adapter
    allCardsName.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it })).forEach {

        listNames.add(it)
    }

    // On affiche l'adapter
    var adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listNames)
    listCardsName.adapter = adapter
}


