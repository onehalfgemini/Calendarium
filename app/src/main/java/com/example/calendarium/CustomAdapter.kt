package com.example.calendarium



import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomAdapter(private val mList: MutableList<ItemViewModel>,
                    private val deleteEvent:(String)->Unit) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {




    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }


    // binds the list items to a view
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]


        holder.deleteButton.setOnClickListener{
            mList.removeAt(position)
            val documentId = mList[position].documentId
            Log.w("Test: ", documentId)
            deleteEvent(documentId)
            notifyDataSetChanged()
        }

        // sets the image to the imageview from our itemHolder class
        holder.timeView.text=(ItemsViewModel.time)
        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text.toString()

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val timeView: TextView = itemView.findViewById(R.id.timeTextView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}