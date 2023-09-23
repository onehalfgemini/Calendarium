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
import android.os.Build
import android.text.Editable
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.calendarium.databinding.ActivityPopUpBinding
import com.example.calendarium.databinding.CardViewDesignBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CalActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView //*
    private lateinit var dateTV: TextView
    private lateinit var binding: ActivityCalBinding
    private lateinit var bindingPop: ActivityPopUpBinding
    private lateinit var delButton: Button
    private lateinit var bindingCard: CardViewDesignBinding
    private lateinit var viewHolder: RecyclerView.ViewHolder



    private val TAG = "EventActivity"
    private val DOW ="FetchActivity"
    private val DEL ="DeleteActivity"
    private val db = FirebaseFirestore.getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()
    lateinit var Date1: String
    lateinit var dzien : String
    lateinit var miesiac:String

    var templist: List<ItemViewModel> = emptyList()
    var data: MutableList<ItemViewModel> = templist.toMutableList()

    private var recyclerview = binding.recyclerView




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
                val timeTempSec = listOf(date, timeTemp.toString())
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

private fun fetchEvent(date:String)
{
    val userId = firebaseAuth.currentUser?.uid

    //findViewById<RecyclerView>(R.id.recyclerview)
    recyclerview.layoutManager = LinearLayoutManager(this)

    if (userId != null) {
        db.collection("users")
            .document(userId)
            .collection("events")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(DOW, "${document.id} => ${document.data}")
                    var note =document.data.toString()
                    var note1 = note.substring(1,note.length-1).split(",")

                    val regex = Regex("noteText=([^,]+)")
                    val matchResult = regex.find(note)
                    var temporal = matchResult?.groups?.get(1)?.value.toString()

                    if (temporal.endsWith("}")) {
                        // Remove the last character using substring
                        temporal = temporal.substring(0, temporal.length - 1)
                    }

//note1[2].split("=")[1])

                    data.add(ItemViewModel( document.id.split(",")[1],temporal, document.id))
                    Log.w("test1", note1[1].split("=")[1])
                }


                val adapter = CustomAdapter(data, this:: deleteEvent)
                recyclerview.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w(DOW, "Error getting documents: ", exception)
            }


        setContentView(binding.root)
    }
}

    private fun deleteEvent(documentId: String) {
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("events")
                .document(documentId)
                .delete()
                .addOnSuccessListener {
                    // Document successfully deleted
                    Log.d(TAG, "DocumentSnapshot successfully deleted: $documentId")

                    // Optionally, remove the item from your data list and notify the adapter
                    val position = data.indexOfFirst { it.documentId == documentId }
                    if (position != -1) {
                        data.removeAt(position)
                        recyclerview.adapter?.notifyItemRemoved(position)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error deleting document $documentId", exception)
                }
        }
    }



    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Calendar.getInstance().time

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        Date1 = LocalDateTime.now().format(formatter)

//        val data = ArrayList<ItemViewModel>()
//        val recyclerview = binding.recyclerView //findViewById<RecyclerView>(R.id.recyclerview)

        calendarView = findViewById(R.id.calendarView)
        dateTV = findViewById(R.id.selectedDateTextView)
        dateTV.setText(Date1)
        fetchEvent(Date1)
        calendarView.setOnDateChangeListener(

            OnDateChangeListener { view, year, month, dayOfMonth ->
                dzien= dayOfMonth.toString()
                miesiac= (month+1).toString()

                if(dayOfMonth<=9) dzien = ("0$dayOfMonth")
                if(month <=8) miesiac = ("0" + (month+1))

                    Date1 = (dzien + "-"
                            + miesiac + "-" + year)
                dateTV.text = Date1
                Log.i("test", Date1)
                fetchEvent(Date1)
            })
        //viewNote(Date) - i wtedy z bazy danych pobiera notatki dla danej daty

        binding.addNoteButton.setOnClickListener {
            addNoteView()
            val note = addNoteText()
            val noteTime = addNoteTime()
            bindingPop.addNoteSubmit.setOnClickListener {

                if (note != null) {
                    createEvent(Date1,  note.toString(), noteTime.toString())
                    setContentView(binding.root)
                    fetchEvent(Date1)
                }
            }
            bindingPop.addNoteBack.setOnClickListener {
                setContentView(binding.root)
            }
        }

//        bindingCard.deleteButton.setOnClickListener{
//
//
//
//         data.removeAt(viewHolder.adapterPosition)
//
//        }

//
//        button.setOnClickListener{deleteItem(index)}


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



