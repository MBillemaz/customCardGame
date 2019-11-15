package com.example.customcardgame.ui.info

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.customcardgame.R

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.ThemeOverlay_Material_Dark)
        setContentView(R.layout.activity_info)
    }
}
