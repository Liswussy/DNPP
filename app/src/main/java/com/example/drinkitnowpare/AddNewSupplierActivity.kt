package com.example.drinkitnowpare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddNewSupplierActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_supplier)

        val db = Firebase.firestore

        val addButton = findViewById<Button>(R.id.btn_add_supp)

        addButton.setOnClickListener {
            val suppliername = findViewById<EditText>(R.id.pt_suppname).text.toString()
            val contactno = findViewById<EditText>(R.id.pt_contact).text.toString()
            val address = findViewById<EditText>(R.id.pt_address).text.toString()
            val email = findViewById<EditText>(R.id.pt_email).text.toString()

            if (suppliername.isNotEmpty() && contactno.isNotEmpty() && address.isNotEmpty() && email.isNotEmpty()) {
                val data = hashMapOf(
                    "address" to address,
                    "contactnum" to contactno,
                    "name" to suppliername,
                    "email" to email
                )

                // Add a new document to the "suppliers" collection with an automatically generated ID
                db.collection("suppliers")
                    .add(data)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(
                            baseContext,
                            "Added Supplier!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Optionally, you can navigate back to the previous screen or perform other actions here.
                        findViewById<EditText>(R.id.pt_suppname).text.clear()
                        findViewById<EditText>(R.id.pt_contact).text.clear()
                        findViewById<EditText>(R.id.pt_address).text.clear()
                        findViewById<EditText>(R.id.pt_email).text.clear()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            baseContext,
                            "Failed to add Supplier!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(this, "Please fill in all supplier information fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

