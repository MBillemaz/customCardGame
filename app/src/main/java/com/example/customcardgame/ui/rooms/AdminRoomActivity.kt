package com.example.customcardgame.ui.rooms

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.peak.salut.Callbacks.SalutDataCallback
import android.util.Log
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.Entities.SalutCard
import com.peak.salut.SalutDevice
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.customcardgame.HostPlayersRoleDetails
import com.example.customcardgame.R
import java.io.ByteArrayOutputStream
import com.example.customcardgame.hostData.CustomHostCardsAdapter
import com.example.customcardgame.hostData.HostCardsdata
import com.example.customcardgame.wifi.SingletonNetwork
import kotlinx.android.synthetic.main.activity_admin_room.*
import kotlinx.android.synthetic.main.fragment_cards.listCards
import kotlin.random.Random

// https://github.com/incognitorobito/Salut#usage

class AdminRoomActivity : AppCompatActivity(), SalutDataCallback {

    // Login utilisé pour la communication entre devices
    lateinit var login: String

    lateinit var customCardAdapter: CustomHostCardsAdapter
    // Récupère la database
    lateinit var db: CardDatabase

    // Toutes les cartes mises dans la partie
    lateinit var allCardsInGame: ArrayList<SalutCard>
    // Tous les joueurs avec la cartes qu'ils ont d'affecté
    lateinit var allPlayerWithRole: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_room)

        login = intent.getStringExtra("login")

        numberPlayer.text = getString(R.string.numberOfPlayer, "0")

        db = Room.databaseBuilder(this, CardDatabase::class.java, "cards")
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

        SingletonNetwork.createNetwork(this, login, true)

        SingletonNetwork.createRoom { device ->
            Log.d(
                this.javaClass.simpleName,
                device.readableName + " has connected!"
            )

            SingletonNetwork.addDevice(device)

            numberPlayer.text = getString(R.string.numberOfPlayer, SingletonNetwork.deviceList.size.toString())
            playerListText.text = getString(
                R.string.playerNames,
                SingletonNetwork.deviceList.map<SalutDevice, String> { it.readableName }.joinToString()
            )
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

        SingletonNetwork.stopNetwork()
    }

    // Quand on reçoie des infos d'autres dispositifs
    // Pour le moment, l'admin ne doit pas recevoir de données des joueurs, cette fonction est donc inutile
    override fun onDataReceived(p0: Any?) {
    }


    // Ajout d'un élément pour simuler la connexion d'un autre préiphérique. Utilisé uniquememnt pour le déboguage
    fun addGhostDevice(): SalutDevice {

        var device = SalutDevice()
        device.readableName = "Ghost player"
        return device
    }


    // Lorsque l'hôte choisis de commencer la partie
    fun onStartClick(view: View) {

        val deviceList = SingletonNetwork.deviceList
        // A COMMENTER LORSQUE L'ON UTILISE D'AUTRES SMARTPHONES ANDROID
        deviceList.add(addGhostDevice())

        allCardsInGame = ArrayList()
        allPlayerWithRole = ArrayList()

        // La partie ne peut démarrer que si on a au moins un joueur et que le nombre de carte
        // est égal au nombre de joueur
        if (deviceList.size > 0 && deviceList.size == customCardAdapter.totalCardNumber) {

            // On crée une liste de device qui sera vidée au fur et à mesure
            val attributionDevice = ArrayList<SalutDevice>()
            attributionDevice.addAll(deviceList)

            // Pour chaque set de carte
            customCardAdapter.dataSource.forEach { hostCard ->
                var card = hostCard.card
                val counter = hostCard.getNumberOfCards()

                // Si cette carte est présente dans la partie
                if (counter > 0) {

                    // On crée un objet carte qui peut être envoyé via Salut
                    val salutCard = SalutCard()
                    salutCard.cardName = card!!.cardName
                    salutCard.description = card!!.description

                    // On s'assure qu'il y ait une photo
                    if (card!!.picture != null) {
                        salutCard.picture = encodeImage(card!!.picture!!)
                    }

                    // Cette carte est utilisée, elle devra être visible par les autres joueurs
                    allCardsInGame.add(salutCard)

                    // On prend un utilisateur au hasard et on lui envoie la carte
                    for (i in 0..counter) {
                        if (attributionDevice.size > 0) {

                            val index = Random.nextInt(0, attributionDevice.size)

                            SingletonNetwork.sendToDevice(attributionDevice[index], salutCard) {

                                try {
                                    Log.e(
                                        javaClass.simpleName,
                                        "Can't send card to device " + attributionDevice[index].instanceName
                                    )
                                }
                                catch (ex: Exception){

                                }
                            }

                            allPlayerWithRole.add(attributionDevice[index].readableName + " - " + salutCard.cardName)

                            // On supprime le device pour ne pas lui envoyer d'autre carte
                            attributionDevice.removeAt(index)
                        }
                    }
                }
            }

            // Toutes les cartes ont été assignées et envoyées, on navigue vers la page pour montrer à l'admin les rôles assignés
            val intent = Intent(this, HostPlayersRoleDetails::class.java)
            intent.putExtra("PlayersRoles", allPlayerWithRole)
            startActivity(intent)

        } else {

            // Affiche un message d'erreur
            val builderSingle = AlertDialog.Builder(this)

            builderSingle.setTitle("Erreur lors du lancement de la partie")
            builderSingle.setMessage("Le nombre de carte est différent du nombre de joueurs présents")

            builderSingle.setNegativeButton("Annuler") { dialog, which -> dialog.dismiss() }

            builderSingle.show()
        }
    }

    // Charge les cartes enregistrées avec bouttons + & - pour ajouter/enlever des cartes
    private fun setCardAdapter(context: Context, listCardsName: ListView) {

        Thread {
            // Récupère tous les noms des cartes
            var allCards = db.cardDao().findAll()
            var listNames = ArrayList<HostCardsdata>(0)

            // Pour chaque nom on l'enregistre dans l'adapter
            allCards.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.cardName }))
                .forEach {

                    listNames.add(HostCardsdata(it))
                }

            // On affiche l'adapter
            customCardAdapter = CustomHostCardsAdapter(listNames, context)

            listCardsName.adapter = customCardAdapter
        }.start()
    }
}
