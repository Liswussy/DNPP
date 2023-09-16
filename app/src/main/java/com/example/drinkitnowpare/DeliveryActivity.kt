package com.example.drinkitnowpare

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale

class DeliveryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delivery_form)

        //placeholder
        val supplier = "Lewis" // Replace with the actual supplier name
        val productName = "Red Horse" // Replace with the actual product name

        //quantity
        val prdQuantityEditText = findViewById<EditText>(R.id.prd_quanity)

        val btn_confirm = findViewById<Button>(R.id.btn_confirm)
        btn_confirm.setOnClickListener{
            val supplier = "Supplier Name" // Replace with the actual supplier name
            val productName = "Product Name" // Replace with the actual product name
            val prdQuantityEditText = findViewById<EditText>(R.id.prd_quanity)

            // Get the current date and time as a Firebase Timestamp
            val currentDate = Timestamp.now()

            // Get the quantity from the EditText and convert it to an Int
            val quantity = prdQuantityEditText.text.toString().toIntOrNull() ?: 0

            // Initialize Firestore
            val db = FirebaseFirestore.getInstance()
            val deliveryReportsCollection = db.collection("delivery_reports")

            // Create a new document with the specified fields
            val report = hashMapOf(
                "supplier" to supplier,
                "product_name" to productName,
                "quantity" to quantity,
                "date" to currentDate
            )

            // Add the document to the "delivery_reports" collection
            deliveryReportsCollection.add(report)
                .addOnSuccessListener { documentReference ->
                    // Document added successfully
                    val reportId = documentReference.id
                    println("Report added with ID: $reportId")
                    Toast.makeText(applicationContext, "Report Created", Toast.LENGTH_SHORT).show()
                    // You can perform any additional actions here, such as displaying a success message
                }
                .addOnFailureListener { e ->
                    // Handle errors here
                    println("Error adding report: $e")
                }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}