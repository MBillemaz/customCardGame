package com.example.customcardgame.ui.in_game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.customcardgame.R
import kotlinx.android.synthetic.main.fragment_cards.view.*

class InGameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_in_game, container, false)

        root.listCards.adapter = null

        return root
    }
}