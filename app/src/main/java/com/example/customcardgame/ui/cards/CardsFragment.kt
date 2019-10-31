package com.example.customcardgame.ui.cards

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.customcardgame.R
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


        root.fabAddCard.setOnClickListener {

            onClickAddCard(context!!, listCards, listItems)
        }


        return root
    }
}


private fun onClickAddCard(context: Context, listCards: ListView, listItems: ArrayList<String>) {

    listItems.add("Nouvelle carte")

    var adapter = ArrayAdapter(context , android.R.layout.simple_list_item_1, listItems)
    listCards.adapter = adapter
}