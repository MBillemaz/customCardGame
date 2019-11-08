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
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.Entities.SalutCard
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.SalutDevice
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.room.RoomDatabase
import com.example.customcardgame.Entities.Card
import java.io.ByteArrayOutputStream
import com.example.customcardgame.hostData.CustomHostCardsAdapter
import com.example.customcardgame.hostData.HostCardsdata
import kotlinx.android.synthetic.main.fragment_cards.*
import kotlin.random.Random

// https://github.com/incognitorobito/Salut#usage

class AdminRoomActivity : AppCompatActivity(), SalutDataCallback {

    // Objet gerant la connexion entre les devices
    lateinit var network: MySalut
    // Login utilisé pour la communication entre devices
    lateinit var login: String
    // Liste des devices connecté
    val deviceList: ArrayList<SalutDevice> = ArrayList()

    lateinit var customCardAdapter: CustomHostCardsAdapter
    // Récupère la database
    lateinit var db: CardDatabase

    // Toutes les cartes mises dans la partie
    lateinit var allCardsInGame: ArrayList<Card>
    // Tous les joueurs avec la cartes qu'ils ont d'affecté
    lateinit var allPlayerWithRole: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_room)

        login = intent.getStringExtra("login")

        db = Room.databaseBuilder(this, CardDatabase::class.java, "cards")
            .allowMainThreadQueries()
            .build()

        // Demande des permissions pour la connexion internet & wifi
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            14541
        )

        // Chargement des cartes
        setCardAdapter(this, listCards)
    }

    // Résultat de la demande de permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Résultat de la demande de permissions
        if (requestCode == 14541) {
            if (grantResults.size == 7
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
                && grantResults[5] == PackageManager.PERMISSION_GRANTED
                && grantResults[6] == PackageManager.PERMISSION_GRANTED
            ) {
                onRequestSuccess()
            }
        }
    }

    // La demande de permissions a été un succès, on continue
    private fun onRequestSuccess() {

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

        // Quand un device se connecte, on l'ajoute à la liste des utilisateurs
        // TODO Affichage de la liste des users
        network.startNetworkService { device ->
            Log.d(
                this.javaClass.simpleName,
                device.readableName + " has connected!"
            )

            deviceList.add(device)
        }
    }

    // Transforme une image en base64 à partir de son URI
    private fun encodeImage(path: String): String {

        val bm = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(path))
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)

    }

    // Lorsque l'on quitte la page
    override fun onStop() {
        super.onStop()

        network.stopNetworkService(false)
    }

    // Quand on reçoie des infos d'autres dispositifs
    // Pour le moment, l'admin ne doit pas recevoir de données des joueurs, cette fonction est donc inutile
    override fun onDataReceived(p0: Any?) {
    }

    // Lorsque l'hôte choisis de commencer la partie
    fun onStartClick(view: View) {

        // Il faut avoir au moins une personne de connecté et autant de cartes que de joueurs
        if (deviceList.size > 0 && deviceList.size == customCardAdapter.totalCardNumber) {
            val attributionDevice = ArrayList<SalutDevice>()
            attributionDevice.addAll(deviceList)

            customCardAdapter.dataSource.forEach { hostCard ->
                var card = hostCard.card
                val counter = hostCard.getNumberOfCards()

                if (counter > 0) {

                    val salutCard = SalutCard()
                    salutCard.cardName = card!!.cardName
                    salutCard.description = card!!.description

                    // On s'assure qu'il y ait une photo
                    if (card!!.picture != null) {
                        salutCard.picture = encodeImage(card!!.picture!!)
                    }

                    for (i in 0..counter) {
                        if (attributionDevice.size > 0) {
                            val index = Random.nextInt(0, attributionDevice.size)

                            network.sendToDevice(attributionDevice[index], salutCard) {
                                Log.e(
                                    javaClass.simpleName,
                                    "Can't send card to device " + attributionDevice[index].instanceName
                                )
                            }
                            attributionDevice.removeAt(index)
                        }
                    }
                }
            }
        } else {
            val builderSingle = AlertDialog.Builder(this)

            builderSingle.setTitle("Erreur lors du lancement de la partie")
            builderSingle.setMessage("Le nombre de carte est différent du nombre de joueurs présents")

            builderSingle.setNegativeButton("Annuler") { dialog, which -> dialog.dismiss() }

            builderSingle.show()
        }
    }

    // Charge les cartes enregistrées avec bouttons + & - pour ajouter/enlever des cartes
    private fun setCardAdapter(context: Context, listCardsName: ListView) {

        // Récupère tous les noms des cartes
        var allCards = db.cardDao().findAll()
        var listNames = ArrayList<HostCardsdata>(0)

        // Pour chaque nom on l'enregistre dans l'adapter
        allCards.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.cardName })).forEach {

            listNames.add(HostCardsdata(it))
        }

        // On affiche l'adapter
        customCardAdapter = CustomHostCardsAdapter(listNames, context)

        listCardsName.adapter = customCardAdapter
    }
}
