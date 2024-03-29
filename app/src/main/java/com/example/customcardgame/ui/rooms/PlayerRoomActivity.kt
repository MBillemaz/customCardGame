package com.example.customcardgame.ui.rooms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.customcardgame.R
import com.example.customcardgame.ui.play.PlayerGameActivity
import com.example.customcardgame.wifi.SingletonNetwork
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.SalutDevice
import kotlinx.android.synthetic.main.activity_player_room.*
import java.io.File


class PlayerRoomActivity : AppCompatActivity() {

    // Login utilisé pour la communication
    lateinit var login: String

    // Chemin de stockage de l'image reçue par le MJ
    // Nécessaire pour la passer jusqu'a la PlayerGameActivity, car la base64 est trop lourde pour passer dans un intent
    private val fileName: String = "playerImage"

    private val deviceList = ArrayList<SalutDevice>()

    private lateinit var arrayAdapter: ArrayAdapter<String>

    private var gameStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.ThemeOverlay_Material_Dark)
        setContentView(R.layout.activity_player_room)

        progressBar.visibility = View.GONE

        login = intent.getStringExtra("login")
        
        arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice)

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
                SingletonNetwork.createNetwork(this, login, false)
                searchRoom()
            }
        }
    }

    fun onSearchRoomButtonClick(view: View) {
        searchRoom()
    }


    fun searchRoom() {
        // Affiche une dialog qui montre les devices trouvés
        val builderSingle = AlertDialog.Builder(this, android.R.style.ThemeOverlay_Material_Dialog_Alert)
        builderSingle.setIcon(android.R.drawable.list_selector_background)
        builderSingle.setTitle("Liste des salles trouvées")

        builderSingle.setNegativeButton("Annuler") { dialog, which ->
            SingletonNetwork.stopFindRoom()
        }

        builderSingle.setAdapter(arrayAdapter) { _, which ->
            val strName = arrayAdapter.getItem(which)
            connectToHost(deviceList[which])
        }
        builderSingle.show()



        // Dés qu'on trouve un device, on essaye de s'y connecter
        SingletonNetwork.findRoom(true) { device ->
            Log.d(
                this.javaClass.simpleName,
                "A device has connected with the name " + device.instanceName
            )

            deviceList.add(device)
            arrayAdapter.add(device.instanceName)

        }
    }

    // Fonction récursive
    // On tente de se connecter dix fois au device trouvé. Si cela échoue, envoie un message d'erreur à l'utilisateur
    fun connectToHost(device: SalutDevice, iteration: Int = 0) {
        SingletonNetwork.joinRoom(
            device,
            SalutCallback {
                Log.d(
                    this.javaClass.simpleName,
                    "Registered !"
                )
                textView.text = "Connecté à ${device.instanceName} \n En attente du début de la partie"
                progressBar.visibility = View.VISIBLE
                waitCard()
            },
            SalutCallback {
                if(iteration < 10) {
                    textView.text = "iteration $iteration"
                    connectToHost(device, iteration + 1)
                } else {
                    textView.text = "Cannot connect to host..."
                }
            }
        )
    }


    // Créer un thread chargé de surveiller la présence d'une carte assignée dans le SingletonNetwork
    // Lors du début de la partie, le MJ va envoyer une carte à l'utilisateur
    // On parse les données reçues, on les transforme en objet SalutCard
    // On stocke l'image dans un fichier local et on lance la PlayerGameActivity
    fun waitCard() {
        Thread {
            while(SingletonNetwork.assignedCard == null) {

            }
            val card = SingletonNetwork.assignedCard
            val intent = Intent(this, PlayerGameActivity::class.java)
            intent.putExtra("cardName", card!!.cardName)
            intent.putExtra("cardDesc", card!!.description)

            val file = File(filesDir, fileName)
            file.writeText(card!!.picture)


            gameStarted = true
            startActivity(intent)
        }.start()
    }

    // Lorsque l'on revient sur cette activité depuis la page suivante, on va directement vers l'écran principal
    override fun onResume() {
        super.onResume()

        if (gameStarted) {
            onBackPressed()
        }
    }
}
