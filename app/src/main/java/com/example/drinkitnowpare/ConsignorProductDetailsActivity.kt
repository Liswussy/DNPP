package com.example.drinkitnowpare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ConsignorProductDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consignor_prd_dtls)

        // Get the document IDs from the intent
        val documentIds = intent.getStringArrayListExtra("documentIds")

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val ordersCollection = db.collection("orders")

        // Initialize a variable to store the total cost
        var totalCost = 0.0

        // Query the "orders" collection to retrieve documents that match the criteria
        if (documentIds != null) {
            ordersCollection
                .get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                    for (document in querySnapshot.documents) {
                        // Retrieve the "products" array from the document
                        val products = document.get("products") as List<Map<String, Any>>

                        println(products)

                        // Iterate through the products
                        for (product in products) {
                            val productID = product["productID"] as String
                            val price = product["price"] as Double
                            val quantity = product["quantity"] as Long

                            // Check if the productID matches one of the document IDs
                            if (documentIds.contains(productID)) {
                                // Calculate the total cost for this product and accumulate it
                                val productTotal = price * quantity
                                totalCost += productTotal
                            }
                        }
                    }

                    // Now, 'totalCost' contains the sum of total costs for the matched products
                    val tv_total_sold = findViewById<TextView>(R.id.tv_total_sold)
                    println(totalCost)
                    tv_total_sold.text = "P $totalCost"
                }
                .addOnFailureListener { exception ->
                    // Handle errors here
                }
        }
    }
}