package com.example.customcardgame

import android.Manifest
import android.content.pm.PackageManager
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
import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.SalutDevice
import kotlinx.android.synthetic.main.activity_player_room.*
import java.io.IOException


class PlayerRoomActivity : AppCompatActivity(), SalutDataCallback {

    lateinit var network: MySalut

    lateinit var login: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_room)

        login = intent.getStringExtra("login")

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

        network = MySalut(dataReceiver, serviceData, SalutCallback {
            Log.d(
                this.javaClass.simpleName,
                "Sorry, but this device does not support WiFi Direct."
            )
        })
        network.isRunningAsHost = false

        network.discoverNetworkServices({ device ->
            Log.d(
                this.javaClass.simpleName,
                "A device has connected with the name " + device.instanceName
            )

            textView.text = device.instanceName

            network.stopServiceDiscovery(false)
        }, false)
    }

    fun connectToHost(device: SalutDevice) {
        var i = 0
        network.registerWithHost(
            device,
            SalutCallback {
                Log.d(
                    this.javaClass.simpleName,
                    "Registered !"
                )
            },
            SalutCallback {
                if(i < 5) {
                    connectToHost(device)
                } else {
                    textView.text = "Cannot connect to host..."
                }
            }
        )
    }


    override fun onDataReceived(data: Any) {
       try {
            val card: Card = LoganSquare.parse(data.toString(), Card::class.java)
       }
       catch (ex: IOException)
       {
           Log.e(this.javaClass.simpleName, "Failed to parse network data.");
       }
    }

    override fun onStop() {
        super.onStop()
        network.unregisterClient(false)
    }
}
