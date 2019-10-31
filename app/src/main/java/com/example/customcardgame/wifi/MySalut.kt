package com.example.customcardgame.wifi

import com.peak.salut.Callbacks.SalutCallback
import com.peak.salut.Salut
import com.peak.salut.SalutDataReceiver
import com.peak.salut.SalutServiceData

class MySalut(dataReceiver: SalutDataReceiver, salutServiceData: SalutServiceData, deviceNotSupported: SalutCallback) : Salut(dataReceiver, salutServiceData, deviceNotSupported) {

    override fun serialize(p0: Any?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}