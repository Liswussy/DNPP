package com.example.drinkitnowpare

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ViewSuppliersActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_suppliers)

        val filter = findViewById<Spinner>(R.id.search_filter)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            arrayOf("Filter", "Name", "Email", "Contact#", "Address")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filter.adapter = adapter

        val button_return = findViewById<Button>(R.id.btn_return)
        button_return.setOnClickListener {
            val intent = Intent(this, ReturnProductActivity::class.java)
            startActivity(intent)
        }

        val linearLayout = findViewById<LinearLayout>(R.id.view_supp)

        val db = Firebase.firestore

        // Reference to the "suppliers" collection
        val suppliersCollection = db.collection("suppliers")

        // Query the "suppliers" collection to retrieve all documents
        suppliersCollection.get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                if (querySnapshot != null) {
                    for (document in querySnapshot.documents) {
                        // Access and handle data here
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val contact = document.getString("contactnum")

                        val nameWithSpacing = "$name \t\t" // Add spaces after name
                        val emailWithSpacing = "$email \t\t" // Add spaces after email

                        val combinedText = "$nameWithSpacing$emailWithSpacing$contact"

                        // Create a TextView to display the data
                        val itemView = TextView(this)
                        itemView.text = combinedText
                        val textSizeInSp = 24 // Set the desired text size in scaled pixels
                        itemView.textSize = textSizeInSp.toFloat()

                        // Add the TextView to the linearLayout
                        linearLayout.addView(itemView)



                    }
                }
            }
            .addOnFailureListener { e ->
                // Handle any errors that occur during data retrieval
                // You can display an error message or handle the error as needed
            }
    }
}
