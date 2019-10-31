package com.example.customcardgame.wifi

import android.util.Log
import com.peak.salut.Callbacks.SalutCallback

class MySalutCallback(val tag: String, val msg: String) : SalutCallback {
    override fun call() {
        Log.d(
            tag,
            msg
        )
    }
}