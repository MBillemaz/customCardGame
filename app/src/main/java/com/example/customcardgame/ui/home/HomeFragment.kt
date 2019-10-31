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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

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