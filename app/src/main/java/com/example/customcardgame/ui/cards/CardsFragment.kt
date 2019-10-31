package com.example.customcardgame.ui.cards

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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
        var adapter: ListAdapter

        var listItems = ArrayList<String>(0)


        // Lors du click pour ajouter une carte
        root.fabAddCard.setOnClickListener {

            // On ajoute la carte
            onClickAddCard(context!!, listCards, listItems)
        }

        // Au click sur une carte
        root.listCards.setOnItemClickListener { parent, view, position, id ->

            openCardDetails(context!!, ((view as TextView).text) as String)
        }


        return root
    }
}


// Ajoute une carte dans la liste
private fun onClickAddCard(context: Context, listCards: ListView, listItems: ArrayList<String>) {

//    listItems.add("Nouvelle carte")
//
//    var adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, listItems)
//    listCards.adapter = adapter

    openCardDetails(context, "")
}


// Ouvre la page des détails d'une carte selon son nom
private fun openCardDetails(context: Context, cardName: String) {

    var intent = Intent(context, CardDetails::class.java)
    intent.putExtra("cardName", cardName)
    context.startActivity(intent)
}

