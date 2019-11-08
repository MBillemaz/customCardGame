package com.example.customcardgame

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.customcardgame.wifi.MySalut
import com.peak.salut.Callbacks.SalutDataCallback
import com.peak.salut.SalutDataReceiver
import com.peak.salut.SalutServiceData
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bluelinelabs.logansquare.LoganSquare
import com.example.customcardgame.Entities.Card
import com.example.customcardgame.Entities.SalutCard
import com.example.customcardgame.ui.play.PlayerGameActivity
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.SalutDevice
import kotlinx.android.synthetic.main.activity_player_room.*
import org.json.JSONObject
import java.io.File
import java.io.IOException


class PlayerRoomActivity : AppCompatActivity(), SalutDataCallback {

    // Objet utilisé pour la connexion entre device
    lateinit var network: MySalut

    // Login utilisé pour la communication
    lateinit var login: String

    // Chemin de stockage de l'image reçue par le MJ
    // Nécessaire pour la passer jusqu'a la PlayerGameActivity, car la base64 est trop lourde pour passer dans un intent
    private val fileName: String = "playerImage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_room)

        login = intent.getStringExtra("login")

        // On demande les permissions de connexion
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

    fun onRequestSuccess() {
        val dataReceiver = SalutDataReceiver(this, this)
        val serviceData = SalutServiceData("CustomCardGame", 50488, login)

        // On initialise la connexion en tant que simple device
        network = MySalut(dataReceiver, serviceData, SalutCallback {
            Log.d(
                this.javaClass.simpleName,
                "Sorry, but this device does not support WiFi Direct."
            )
        })
        network.isRunningAsHost = false

        // Dés qu'on trouve un device, on essaye de s'y connecter
        network.discoverNetworkServices({ device ->
            Log.d(
                this.javaClass.simpleName,
                "A device has connected with the name " + device.instanceName
            )


            connectToHost(device)

            network.stopServiceDiscovery(false)
        }, false)
    }

    // Fonction récursive
    // On tente de se connecter cinq fois au device trouvé. Si cela échoue, envoie un message d'erreur à l'utilisateur
    fun connectToHost(device: SalutDevice, iteration: Int = 0) {
        network.registerWithHost(
            device,
            SalutCallback {
                Log.d(
                    this.javaClass.simpleName,
                    "Registered !"
                )
                textView.text = device.instanceName
            },
            SalutCallback {
                if(iteration < 5) {
                    textView.text = "iteration " + iteration
                    connectToHost(device, iteration + 1)
                } else {
                    textView.text = "Cannot connect to host..."
                }
            }
        )
    }


    // Lors du début de la partie, le MJ va envoyer une carte à l'utilisateur
    // On parse les données reçues, on les transforme en objet SalutCard
    // On stocke l'image dans un fichier local et on lance la PlayerGameActivity
    override fun onDataReceived(data: Any) {
        Log.d(javaClass.simpleName, "Nouvelles données :")
       try {
           val card: SalutCard = LoganSquare.parse(data.toString(), SalutCard::class.java)
           val intent = Intent(this, PlayerGameActivity::class.java)
           intent.putExtra("cardName", card!!.cardName)
           intent.putExtra("cardDesc", card!!.description)

           val file = File(filesDir, fileName)
           file.writeText(card!!.picture)

           startActivity(intent)
       }
       catch (ex: IOException)
       {
           Log.e(this.javaClass.simpleName, "Failed to parse network data.");
       }
    }

    // Quand on quitte l'activity, on close la connexion
    override fun onStop() {
        super.onStop()
        network.unregisterClient(false)
    }
}
