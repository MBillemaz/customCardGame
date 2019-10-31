package com.example.customcardgame.ui.home

import android.content.Context
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

        root.userRoomButton.setOnClickListener { view ->
            onUserClick()
        }

        root.adminRoomButton.setOnClickListener { view ->
            onAdminClick()
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val storedLogin = sharedPref?.getString(getString(R.string.login_key), null)

        root.login.setText(storedLogin)

        return root
    }

    fun onUserClick() {
        storeLogin()
        val intent = Intent(context, PlayerRoomActivity::class.java)
        intent.putExtra("login", login.text.toString())
        startActivity(intent)
    }

    fun onAdminClick() {
        storeLogin()
        val intent = Intent(context, AdminRoomActivity::class.java)
        intent.putExtra("login", login.text.toString())
        startActivity(intent)
    }

    fun storeLogin() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(getString(R.string.login_key), login.text.toString())
            commit()
        }
    }
}