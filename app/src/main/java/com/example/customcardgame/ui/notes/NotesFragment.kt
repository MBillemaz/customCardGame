package com.example.customcardgame.ui.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.customcardgame.R
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.fragment_notes.view.*

class NotesFragment : Fragment() {

//    private lateinit var notesViewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notes, container, false)


        // On récupère dans les préférences les notes enregistrées s'il y en a...
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val storedNotes = sharedPref?.getString(getString(R.string.notes_key), null)

        // ... et on les affiches
        root.edtNotes.setText(storedNotes)

        return root
    }

    // Lorsque l'on quitte l'activité on enregistre les notes
    override fun onPause() {
        super.onPause()


        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        with(sharedPref.edit()) {
            putString(getString(R.string.notes_key), edtNotes.text.toString())
            commit()
        }
    }
}