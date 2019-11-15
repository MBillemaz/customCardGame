package com.example.customcardgame.ui.cardDetails

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.customcardgame.Database.CardDatabase
import com.example.customcardgame.Entities.Card
import com.example.customcardgame.R
import com.example.customcardgame.ui.play.PlayerGameActivity
import kotlinx.android.synthetic.main.activity_card_details.*

class CardDetails : AppCompatActivity() {

    var oldName: String = ""

    var card: Card = Card()

    private val PERMISSION_CODE = 1001

    //image pick code
    private val IMAGE_PICK_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.ThemeOverlay_Material_Dark)
        setContentView(R.layout.activity_card_details)


        // On récupère et on affiche le nom de la carte
        oldName = intent.getStringExtra("cardName")!!
        edtCardName.setText(oldName)

        // On cherche la description et l'image dans la BDD
        var bddCard = getCardFromBDD(oldName)

        if (bddCard != null) {

            card = bddCard
            edtDescription.setText(card.description)

            //permission already granted
            if(card.picture != null) {

                imageButton.setImageURI(Uri.parse(card.picture))
            }
        }


        // Choix de l'image
        imageButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    requestStoragePermission(PERMISSION_CODE)
                } else {
                    //permission already granted
                    pickImageFromGallery()
                }
            } else {
                //system OS is < Marshmallow
                pickImageFromGallery()
            }
        }


        // Quand on clique sur le bouton pour annuler on ne valide pas les modifs
        btnCancel.setOnClickListener {

            // On revient à l'écran précédent
             super.onBackPressed()
        }

        // Quand on clique sur le bouton pour valider on valide les modifs
        btnValid.setOnClickListener {

            // Il faut avoir donné un nom à la carte
            if (edtCardName.text.toString().isNotBlank()) {

                // Si on a réussi à sauvegarder la carte, on revient à l'écran précédent
                if (saveCard()) {

                    super.onBackPressed()
                }
            } else {

                Toast.makeText(
                    this,
                    "Le nom de la carte est obligatoire et doit être unique",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    fun requestStoragePermission(code: Int) {
        //permission denied
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        //show popup to request runtime permission
        requestPermissions(permissions, code)
    }

    private fun pickImageFromGallery() {

        //Intent to pick image
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            IMAGE_PICK_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    imageButton.setImageURI(Uri.parse(card.picture))
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {

            imageButton.setImageURI(data?.data)
            card.picture = data?.data.toString()
        }
    }


    // Enregsitre une carte dans la BDD
    private fun saveCard(): Boolean {

        val db = Room.databaseBuilder(applicationContext, CardDatabase::class.java, "cards")
            .allowMainThreadQueries()
            .build()


        var newName = edtCardName.text.toString()
        var description = edtDescription.text.toString()

        var thisCard: Card? = db.cardDao().findByName(oldName)

        // Si le nom a été changé on vérifie qu'une autre carte n'a pas le même nouveau nom
        if (oldName != newName) {

            // Si le nouveau nom existe déjà on ne l'ajoute pas
            if (db.cardDao().findByName(newName) != null) {

                // Le nouveau nom est déjà pris, on affiche une erreur
                Toast.makeText(this, "Vous avez déjà une carte avec ce nom !", Toast.LENGTH_SHORT)
                    .show()

                return false
            }
        }

        // Si on avait une carte on la supprime avant d'en recréer une neuve
        if (thisCard != null) {

            // On supprime d'abord l'ancienne pour ne pas créer une carte alors qu'on en modifie une
            db.cardDao().delete(thisCard)
        }


        var newCard = Card()

        newCard.cardName = newName
        newCard.description = description
        newCard.picture = card.picture

        db.cardDao().insertAll(newCard)

        return true
    }

    // Récupère les infos d'une carte dans la BDD
    private fun getCardFromBDD(oldName: String): Card? {


        val db = Room.databaseBuilder(applicationContext, CardDatabase::class.java, "cards")
            .allowMainThreadQueries()
            .build()

        return db.cardDao().findByName(oldName)
    }
}
