package com.example.customcardgame

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.peak.salut.Callbacks.SalutDataCallback
import com.peak.salut.SalutServiceData
import com.peak.salut.SalutDataReceiver
import com.example.customcardgame.wifi.MySalut
import com.example.customcardgame.wifi.MySalutCallback
import android.util.Log
import androidx.core.app.ActivityCompat


class AdminRoomActivity : AppCompatActivity(), SalutDataCallback  {

    lateinit var network: MySalut

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_room)

        ActivityCompat.requestPermissions(this@AdminRoomActivity,
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
                val dataReceiver = SalutDataReceiver(this, this)
                val serviceData = SalutServiceData("sas", 50489, "ADMIN")

                network = MySalut(dataReceiver, serviceData, MySalutCallback(
                    this.javaClass.simpleName,
                    "Sorry, but this device does not support WiFi Direct."
                ))
                network.isRunningAsHost = true

                network.startNetworkService { device ->
                    Log.d(
                        this.javaClass.simpleName,
                        device.readableName + " has connected!"
                    )
                }
            }
        }
    }

    override fun onDataReceived(p0: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        network.stopNetworkService(false)
    }
}
