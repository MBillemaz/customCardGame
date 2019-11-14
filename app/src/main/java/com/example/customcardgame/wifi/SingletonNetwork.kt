package com.example.customcardgame.wifi

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.example.customcardgame.MainActivity
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.Callbacks.SalutDataCallback
import com.peak.salut.Callbacks.SalutDeviceCallback
import com.peak.salut.SalutDataReceiver
import com.peak.salut.SalutDevice
import com.peak.salut.SalutServiceData
import java.lang.Exception

object SingletonNetwork: SalutDataCallback {

    lateinit var network: MySalut

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

    fun createRoom(callback: (device: SalutDevice) -> Unit) {
        network.startNetworkService(callback)
    }

    fun sendToDevice(device: SalutDevice, message: Any, onFailure: () -> Unit) {
        network.sendToDevice(device, message, onFailure)
    }

    fun findRoom( callContinuously: Boolean, callback: (SalutDevice) -> Unit) {
        network.discoverNetworkServices(callback, callContinuously)
    }

    fun joinRoom(device: SalutDevice, onSuccess: SalutCallback, onFailure: SalutCallback) {
        network.registerWithHost(device, onSuccess, onFailure)
    }



    override fun onDataReceived(data: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}