package com.example.customcardgame.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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

        // Lors du clique sur le bouton de connexion client
        root.userRoomButton.setOnClickListener { view ->
            onUserClick(context!!)
        }

        // Lors du clique sur le bouton de connexion de l'hôte
        root.adminRoomButton.setOnClickListener { view ->
            onAdminClick(context!!)
        }

        // On récupère le login précédemment enregistré par l'utilisateur actuel (si par exemple il a déjà joué)...
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val storedLogin = sharedPref?.getString(getString(R.string.login_key), null)

        // ... pour l'afficher dans la textBox
        root.login.setText(storedLogin)

        return root
    }

    // Ouvre la page de l'utilisateur client
    private fun onUserClick(context: Context) {

        // Si le login a été bien enregistré
        if (storeLogin(context)) {

            // On ouvre la page de l'utilisateur client
            val intent = Intent(context, PlayerRoomActivity::class.java)

            intent.putExtra("login", login.text.toString())
            startActivity(intent)
        }
    }

    // Ouvre la page de l'hôte de la partie
    private fun onAdminClick(context: Context) {

        // Si le login a été bien enregistré
        if (storeLogin(context)) {

            // On ouvre la page de l'admin
            val intent = Intent(context, AdminRoomActivity::class.java)

            intent.putExtra("login", login.text.toString())
            startActivity(intent)
        }
    }

    // Enregistre le login de l'utilisateur
    private fun storeLogin(context: Context): Boolean {

        // On récupère le login et on enlève les espaces situés au début ou à la fin
        val loginText = login.text.toString().trim()

        // Si l'utilisateur a mal écrit son login
        if (loginText.isNullOrBlank()) {

            // On affiche une erreur
            login.setError("Il faut un nom d'utilisateur.")

            return false

        } else if(loginText.length > 15) {
            login.setError("Le login est limité à 15 caractères")
            return false
        }
        else {

            // On enregistre l'identifiant
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return false

            with(sharedPref.edit()) {
                putString(getString(R.string.login_key), loginText)
                commit()
            }

            return true
        }
    }
}