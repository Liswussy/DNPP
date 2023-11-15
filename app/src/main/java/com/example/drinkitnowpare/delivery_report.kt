package com.example.drinkitnowpare

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.Locale

class delivery_report : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_report)

        val linearLayout = findViewById<LinearLayout>(R.id.layout)

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val deliveryReportsCollection = db.collection("delivery_reports")

        // Query the "delivery_reports" collection to retrieve all documents
        deliveryReportsCollection.get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document in querySnapshot.documents) {
                    // Get data from all fields in each document
                    val supplier = document.getString("supplier")
                    val products = document.get("products") as List<Map<String, Any>>?
                    // val quantity = document.getLong("quantity")?.toInt() ?: 0
                    val dateTimestamp = document.getTimestamp("date")

                    // Format the date
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val date = dateTimestamp?.toDate()?.let { dateFormat.format(it) }

                    // Inflate the layout for each document
                    val inflater = LayoutInflater.from(this)
                    val itemView = inflater.inflate(R.layout.delivery_report_item, null)

                    // Populate the inflated layout with the collected data
                    val supplierTextView = itemView.findViewById<TextView>(R.id.tv_supp)
                    supplierTextView.text = supplier

                    var productsText = ""

                    val productNameTextView = itemView.findViewById<TextView>(R.id.prd_name)
                    if (products != null) {
                        for (map in products) {
                            val prodID = map["prodID"]
                            val qty = map["qty"].toString().toInt()

                            if (qty > 0){
                                val docRef = db.collection("products").document(prodID as String)
                                docRef.get()
                                    .addOnSuccessListener { documentSnapshot ->
                                        if (documentSnapshot.exists()) {
                                            val prdnme = documentSnapshot.getString("prdnme")

                                            val text = "\n${prdnme}: $qty"
                                            productsText += text
                                            productNameTextView.text = productsText.toString()
                                        } else {
                                            println("Document does not exist")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        println("Error getting document: $e")
                                    }


                            }

                        }
                    }




//                    val quantityTextView = itemView.findViewById<TextView>(R.id.tv_qty)
//                    quantityTextView.text = quantity.toString()

                    val dateTextView = itemView.findViewById<TextView>(R.id.tv_date)
                    dateTextView.text = date

                    // Add the inflated layout to the LinearLayout
                    linearLayout.addView(itemView)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }
}