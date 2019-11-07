package com.example.customcardgame.ui.play

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_player_game.*
import android.view.animation.DecelerateInterpolator
import android.animation.ObjectAnimator
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import androidx.core.view.marginTop
import com.example.customcardgame.R
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.File


class PlayerGameActivity : AppCompatActivity() {

    private var isLongPressed = false
    private var backImageInitialTop: Int = 0
    private val fileName = "playerImage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.customcardgame.R.layout.activity_player_game)

        cardName.text = intent.getStringExtra("cardName")!!
        cardDesc.text = intent.getStringExtra("cardDesc")!!


        val file = File(filesDir, fileName)
        if(file.exists()){
            val base64Image = file.readText()

            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            playerImage.setImageBitmap(decodedByte)
        }



        backImageInitialTop = backImage.marginTop

        backImage.setOnLongClickListener{

            isLongPressed = true

            val oa1 = ObjectAnimator.ofFloat(backImage, "y", backImageInitialTop.toFloat(), backImage.bottom.toFloat())
            oa1.interpolator = DecelerateInterpolator()
            oa1.setDuration(300)
            oa1.start()

            true
        }

        backImage.setOnTouchListener { view: View, motionEvent: MotionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_UP && isLongPressed) {
                isLongPressed = false


                val oa1 = ObjectAnimator.ofFloat(backImage, "y",backImage.bottom.toFloat(), backImageInitialTop.toFloat())
                oa1.interpolator = DecelerateInterpolator()
                oa1.setDuration(300);
                oa1.start()
            }
            false
        }



    }

    fun flipImage(setBackImage: Boolean) {

    }

   /* fun flipImage(setBackImage: Boolean) {
        val oa1 = ObjectAnimator.ofInt(playerImage, "layout_marginTop", initialTop, )
        val oa2 = ObjectAnimator.ofFloat(playerImage, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.setDuration(300);
        oa2.setDuration(300);
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if(setBackImage) {
                    playerImage.setImageResource(R.drawable.back_card)
                } else {
                    playerImage.setImageURI(image)
                }
                oa2.start()
            }
        })
        oa1.start()
    }
    */

}
