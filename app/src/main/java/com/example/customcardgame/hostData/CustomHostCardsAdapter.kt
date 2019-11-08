package com.example.customcardgame.hostData

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.example.customcardgame.Entities.Card
import com.example.customcardgame.R


class CustomHostCardsAdapter(val dataSource: ArrayList<HostCardsdata>, val context: Context) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var totalCardNumber = 0

    lateinit var card: Card

    // Récupère la taille de notre liste
    override fun getCount(): Int {
        return dataSource.size
    }

    // Récupère l'item selon sa position
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    // Récupère la position de l'item en type Long
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // Récupère la vue et crée ses contraintes
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // Get view for row item
        val rowView = inflater.inflate(R.layout.host_card_item, parent, false)

        // Récupère CardName
        val cardName = rowView.findViewById(R.id.CardName) as TextView
        // Récupère le nombre
        val numberOfCards = rowView.findViewById(R.id.NumberOfCards) as TextView
        // Le bouton -
        val minusButton = rowView.findViewById(R.id.minus) as Button
        // Le bouton +
        val plusButton = rowView.findViewById(R.id.plus) as Button
        

        // Lors du clique sur le bouton '-'
        minusButton.setOnClickListener {

            val decreased = dataSource.get(position).decreaseNumberOfCards()
            numberOfCards.text = dataSource.get(position).getNumberOfCards().toString()

            if (decreased) { totalCardNumber-- }

        }

        // Lors du clique sur le bouton '+'
        plusButton.setOnClickListener {

            val increase = dataSource.get(position).increaseNumberOfCards()
            if (increase) { totalCardNumber++ }
            numberOfCards.text = dataSource.get(position).getNumberOfCards().toString()
        }

        val cardData = getItem(position) as HostCardsdata
        cardName.text = cardData.getCardName()
        numberOfCards.text = 0.toString()

        return rowView
    }


}
