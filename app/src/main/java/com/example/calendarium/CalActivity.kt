package com.example.calendarium

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.calendarium.databinding.ActivityCalBinding
import android.widget.CalendarView
import android.widget.TextView
import android.widget.CalendarView.OnDateChangeListener
import com.google.firebase.auth.FirebaseAuth
import android.icu.util.Calendar
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

import java.util.HashMap

import com.example.calendarium.databinding.ActivityPopUpBinding



class CalActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView //*
    private lateinit var dateTV: TextView
    private lateinit var binding: ActivityCalBinding
    private lateinit var bindingPop: ActivityPopUpBinding

    private val TAG = "CalActivity" // Deklaracja zmiennej TAG
    private val DOW ="FetchActivity"
    private val db = FirebaseFirestore.getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()
    lateinit var Date1: String


    data class Event(
        val title: String,
        val date: String,
        val description: String,
        var noteText: String? = null,
        var noteTime: String? = null
    )

    private fun viewNote() {
        //TODO: POBIERANIE Z BAZY DO LISTVIEW \"@+id/notesListView\"
        //TODO: GUZIK DODAJ MA DZIAŁAĆ ID "@+id/addNoteButton", , GODZINA TEŻ
        //TODO:
        //ZROBIĆ LAYOUT EDITTEXT-> pop_up
    }


    private fun addNoteView(){
        bindingPop = ActivityPopUpBinding.inflate(layoutInflater)
        setContentView(bindingPop.root)
    }
    private fun addNoteText(): Editable? {
        val editTextNote = bindingPop.addNoteEditText
        val note = editTextNote.text
        return note
    }
    private fun addNoteTime(): Editable? {
        val timePickerNote = bindingPop.addNoteTimePicker
        var time: Editable? = null


        time = Editable.Factory.getInstance().newEditable("${timePickerNote.hour}:${timePickerNote.minute}")

        timePickerNote.setOnTimeChangedListener { _, hour, minute ->

            time?.clear()
            time?.append("$hour:$minute")
        }

        return time
    }


    private fun createEvent( date: String,  noteText: String, noteTime: String) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            // Pobieramy notatkę i czas z funkcji addNoteText i addNoteTime
            val noteText = addNoteText()
            val noteTime = addNoteTime()

            if (noteText != null && noteTime != null) {
                val event = hashMapOf(
                    "date" to date,
                    "noteText" to noteText.toString(),
                    "noteTime" to noteTime.toString()
                )

                val timeTemp= addNoteTime()
                val timeTempSec = listOf(Date1, timeTemp.toString())
                var docID = timeTempSec.joinToString(",")

                // Dodaj nowe wydarzenie do kolekcji "events" użytkownika
                db.collection("users")
                    .document(userId)
                    .collection("events").document(docID).set(event)
                    .addOnSuccessListener { documentReference ->
                        // Operacja zakończona sukcesem
                        Log.d(TAG, "DocumentSnapshot added with ID: ${docID}")
                    }
                    .addOnFailureListener { e ->
                        // Błąd podczas dodawania dokumentu
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }

private fun fetchEvent()
{
    val userId = firebaseAuth.currentUser?.uid

    if (userId != null) {
        db.collection("users")
            .document(userId)
            .collection("events")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(DOW, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(DOW, "Error getting documents: ", exception)
            }
    }
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Calendar.getInstance().time

        val data = ArrayList<ItemViewModel>()
        val recyclerview = binding.recyclerView //findViewById<RecyclerView>(R.id.recyclerview)

        calendarView = findViewById(R.id.calendarView)
        dateTV = findViewById(R.id.selectedDateTextView)
        calendarView.setOnDateChangeListener(
            OnDateChangeListener { view, year, month, dayOfMonth ->
                Date1 = (dayOfMonth.toString() + "-"
                        + (month + 1) + "-" + year)
                dateTV.setText(Date1)
            })
        //viewNote(Date) - i wtedy z bazy danych pobiera notatki dla danej daty

        fetchEvent()


        binding.addNoteButton.setOnClickListener {
            addNoteView()
            val note = addNoteText()
            val noteTime = addNoteTime()
            bindingPop.addNoteSubmit.setOnClickListener {
                if (note != null) {
                    recyclerview.layoutManager = LinearLayoutManager(this)
                    data.add(ItemViewModel(R.drawable.ic_baseline_folder_24, note))
                    val adapter = CustomAdapter(data)
                    recyclerview.adapter = adapter
                    setContentView(binding.root)

                    val date = Date1


                    createEvent( date,  note.toString(), noteTime.toString())


                }
            }
            bindingPop.addNoteBack.setOnClickListener {
                setContentView(binding.root)
            }
        }

//        val formattedDate = formatDate(currentDate.year, currentDate.month, currentDate.day)
//        onDateSelected(formattedDate)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.btnLogout.setOnClickListener{
            firebaseAuth.signOut()
            val logoutIntent = Intent(this, LogInActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
            finish()
        }
    }

}

