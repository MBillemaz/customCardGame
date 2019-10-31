package com.example.customcardgame.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.customcardgame.AdminRoomActivity
import com.example.customcardgame.PlayerRoomActivity
import com.example.customcardgame.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)

        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })

        root.button.setOnClickListener { view ->
            onUserClick()
        }

        root.button2.setOnClickListener { view ->
            onAdminClick()
        }

        return root
    }

    fun onUserClick() {
        val intent = Intent(context, PlayerRoomActivity::class.java)
        startActivity(intent)
    }

    fun onAdminClick() {
        val intent = Intent(context, AdminRoomActivity::class.java)
        startActivity(intent)
    }
}