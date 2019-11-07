package com.example.customcardgame

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.peak.salut.Callbacks.SalutDataCallback
import com.peak.salut.SalutServiceData
import com.peak.salut.SalutDataReceiver
import com.example.customcardgame.wifi.MySalut
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.hostData.CustomHostCardsAdapter
import com.example.customcardgame.hostData.HostCardsdata
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.SalutDevice
import kotlinx.android.synthetic.main.fragment_cards.*

// https://github.com/incognitorobito/Salut#usage

class AdminRoomActivity: AppCompatActivity(), SalutDataCallback{

    lateinit var network: MySalut

    lateinit var login: String

    val deviceList: ArrayList<SalutDevice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_room)

        login = intent.getStringExtra("login")

        // Demande des permissions pour la connexion internet & wifi
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
            ),
            14541
        )

        // Chargement des cartes
        loadAllCards(this, listCards)
    }

    // Résultat de la demande de permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Résultat de la demande de permissions
        if(requestCode == 14541){
            if (grantResults.size == 5
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED)
            {
                onRequestSuccess()
            }
        }
    }

    // La demande de permissions a été un succès, on continue
    fun onRequestSuccess() {

        val dataReceiver = SalutDataReceiver(this, this)
        val serviceData = SalutServiceData("CustomCardGame", 50488, login)

        // Création du salon en tant qu'hôte
        network = MySalut(dataReceiver, serviceData, SalutCallback {
            Log.d(
                this.javaClass.simpleName,
            "Sorry, but this device does not support WiFi Direct."
            )
        })
        network.isRunningAsHost = true

        network.startNetworkService { device ->
            Log.d(
                this.javaClass.simpleName,
                device.readableName + " has connected!"
            )
            deviceList.add(device)
        }
    }

    // Lorsque l'on quitte la page
    override fun onStop() {
        super.onStop()

        network.stopNetworkService(false)
    }

    // Quand on reçoie des infos d'autres dispositifs
    override fun onDataReceived(p0: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    // Charge les cartes enregistrées avec bouttons + & - pour ajouter/enlever des cartes
    private fun loadAllCards(context: Context, listCardsName: ListView) {

        // Récupère la database
        val db = Room.databaseBuilder(context, CardDatabase::class.java, "cards")
            .allowMainThreadQueries()
            .build()

        // Récupère tous les noms des cartes
        var allCardsName = db.cardDao().getAllNames()
        var listNames = ArrayList<HostCardsdata>(0)

        // Pour chaque nom on l'enregistre dans l'adapter
        allCardsName.forEach {

            listNames.add(HostCardsdata(it))
        }

        // On affiche l'adapter
        var adapter = CustomHostCardsAdapter(listNames, context)
        listCardsName.adapter = adapter
    }
}
