package com.example.customcardgame.wifi

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.bluelinelabs.logansquare.LoganSquare
import com.example.customcardgame.Entities.SalutCard
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.Callbacks.SalutDataCallback
import com.peak.salut.SalutDataReceiver
import com.peak.salut.SalutDevice
import com.peak.salut.SalutServiceData

// Singleton gérant la connexion entre devices
object SingletonNetwork: SalutDataCallback {

    lateinit var network: MySalut

    // Liste des devices connecté
    val deviceList: MutableMap<String, SalutDevice> = mutableMapOf()

    // Toutes les cartes mises dans la partie
    var allCardsInGame: ArrayList<SalutCard> = ArrayList()

    // Toutes les cartes mises dans la partie
    var uniqueCardInGame: ArrayList<SalutCard> = ArrayList()

    var assignedCard: SalutCard? = null

    //Initialise la connexion
    fun createNetwork(context: Context, login: String, isHost: Boolean) {

        val dataReceiver = SalutDataReceiver(context, this)
        val serviceData = SalutServiceData("CustomCardGame", 50488, login)

        network = MySalut(dataReceiver, serviceData, SalutCallback {
            Log.d(
                this.javaClass.simpleName,
                "Sorry, but this device does not support WiFi Direct."
            )
        })

        network.isRunningAsHost = isHost
    }

    // Ferme la connexion
    fun stopNetwork() {
        if(network.isRunningAsHost) {
            network.stopNetworkService(false)
        }
        else {
            if(network.registeredHost != null) {
                network.unregisterClient(false)
            }
        }
    }

    // Créer un salon en wifi direct
    fun createRoom(registerCallback: (device: SalutDevice) -> Unit, unregisterCallback: (device: SalutDevice) -> Unit) {
        network.startNetworkService(
            registerCallback,
            { Log.d(TAG, "Network service started")},
            { Log.d(TAG, "Network service failed")}
        )
        network.setOnDeviceUnregisteredCallback(unregisterCallback)
    }

    // Envoie un message à un device
    fun sendToDevice(device: SalutDevice, message: Any, onFailure: () -> Unit) {
        network.sendToDevice(device, message, onFailure)
    }

    // Cherche un salon existant
    fun findRoom( callContinuously: Boolean, callback: (SalutDevice) -> Unit) {
        network.discoverNetworkServices(callback, callContinuously)
    }

    fun stopFindRoom() {
        network.stopServiceDiscovery(true)
    }

    // Rejoint un salon existant
    fun joinRoom(device: SalutDevice, onSuccess: SalutCallback, onFailure: SalutCallback) {
        network.registerWithHost(device, onSuccess, onFailure)
    }

    // Ajoute un device à la liste des clients
    fun addDevice(device: SalutDevice) {
        deviceList.put(device.readableName, device)
    }

    // Ajoute un device à la liste des clients
    fun removeDevice(device: SalutDevice) {
        deviceList.remove(device.readableName)
    }


    override fun onDataReceived(data: Any?) {
        assignedCard = LoganSquare.parse(data.toString(), SalutCard::class.java)
    }
}