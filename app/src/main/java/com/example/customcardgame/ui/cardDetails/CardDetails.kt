package com.example.customcardgame.ui.cardDetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.Entities.Card
import com.example.customcardgame.R
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetails : AppCompatActivity() {

    var oldName: String = ""

    var card: Card = Card()

    private val PERMISSION_CODE = 1001;

    //image pick code
    private val IMAGE_PICK_CODE = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)


        // On récupère et on affiche le nom de la carte
        oldName = intent.getStringExtra("cardName")!!
        edtCardName.setText(oldName)

        imageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }

        // Quand on clique sur le bouton pour annuler on ne valide pas les modifs
        btnCancel.setOnClickListener {

            super.onBackPressed()
        }

        // Quand on clique sur le bouton pour valider on valide les modifs
        btnValid.setOnClickListener {

            saveCard()
            super.onBackPressed()
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            imageButton.setImageURI(data?.data)
            card.picture = data?.data.toString()
        }
    }


    // Enregsitre une carte dans la BDD
    private fun saveCard() {

        val db = Room.databaseBuilder(applicationContext, CardDatabase::class.java, "cards")
            .allowMainThreadQueries()
            .build()

        var newName = edtCardName.text.toString()
        var description = edtDescription.text.toString()

        var thisCard: Card = db.cardDao().findByName(oldName)

        // Si le nom a été changé on vérifie qu'une autre carte n'a pas le même nouveau nom
        if (oldName != newName) {

            // Si le nouveau nom n'existe pas encore on l'ajoute
            if (db.cardDao().findByName("zertzert") == null) {

            }
            // Si le nouveau nom existe déjà on affiche une erreur
            else {

            }
        }
    }
}
