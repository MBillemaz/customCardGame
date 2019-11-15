package com.example.customcardgame.ui.play

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import kotlinx.android.synthetic.main.activity_player_game.*
import java.io.File


class PlayerGameActivity : AppCompatActivity() {

    // Variable indiquant si l'utilisateur est entrain de toucher l'écran
    private var isLongPressed = false

    // Contient la position de base de l'image
    // Utilisé pour la remettre à sa place
    private var backImageInitialTop: Int = 0

    // Chemin du fichier contenant l'image à afficher
    private val fileName = "playerImage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.ThemeOverlay_Material_Dark)
        setContentView(com.example.customcardgame.R.layout.activity_player_game)

        // On récupere les informations de la carte dans l'intent et le fichier
        cardName.text = intent.getStringExtra("cardName")!!
        cardDesc.text = intent.getStringExtra("cardDesc")!!


        // Si on à une image stockée, on la récupère et on l'affiche.
        val file = File(filesDir, fileName)
        if(file.exists()){
            val base64Image = file.readText()

            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            playerImage.setImageBitmap(decodedByte)
        }

        // On récupere la position initiale de l'image
        backImageInitialTop = backImage.marginTop

        // Quand l'utilisateur fait un clic long sur l'image, on lance une animation pour la faire descendre et afficher
        // Ce qu'il y a derrière
        backImage.setOnLongClickListener{

            isLongPressed = true

            val oa1 = ObjectAnimator.ofFloat(backImage, "y", backImageInitialTop.toFloat(), backImage.bottom.toFloat())
            oa1.interpolator = DecelerateInterpolator()
            oa1.setDuration(300)
            oa1.start()

            true
        }

        sendVibration()

        // Uniquement quand l'utilisateur relache un clic long, on lance une animation
        // Pour remettre l'image à sa place
        backImage.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP && isLongPressed) {
                isLongPressed = false


                val oa1 = ObjectAnimator.ofFloat(backImage, "y",backImage.bottom.toFloat(), backImageInitialTop.toFloat())
                oa1.interpolator = DecelerateInterpolator()
                oa1.setDuration(300)
                oa1.start()
            }
            false
        }



    }

    fun sendVibration() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(1000, 1))
        } else{
            vibrator.vibrate(1000)
        }

    }

}
