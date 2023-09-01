package com.example.drinkitnowpare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class SalesReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sales_report)

        val scrollContainer: LinearLayout = findViewById(R.id.scrollContainer)


        val firestore = FirebaseFirestore.getInstance()

        val ordersCollection = firestore.collection("orders")

        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

        ordersCollection.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Access each document's data here
                    val documentData = document.data
                    val timestamp = document.getTimestamp("timestamp")
                    val total = document.getDouble("total")
                    val products = document.get("products") as List<Map<String, Any>>?

                    val presetView: View = LayoutInflater.from(this@SalesReportActivity).inflate(R.layout.sales_report_item, null)
                    val dateTextView: TextView = presetView.findViewById(R.id.dateTextView)
                    val productsTextView: TextView = presetView.findViewById(R.id.productsTextView)
                    val amountTextView: TextView = presetView.findViewById(R.id.amountTextView)

                    val params =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    params.setMargins(0, 0, 0, 20);
                    presetView.layoutParams = params


                    if (timestamp != null) {
                        // Convert the timestamp to a formatted date string
                        val dateString = dateFormat.format(timestamp.toDate())
                        dateTextView.text = dateString
                    }

                    var quantityString: String = ""
                    if (products != null) {
                        for (productMap in products) {
                            val productName = productMap["productName"] as String
                            val quantity = productMap["quantity"] as Long

                            val prod = "$productName: $quantity\n"
                            quantityString += prod
                        }
                    }

                    productsTextView.text = quantityString
                    amountTextView.text = total.toString()

                    scrollContainer.addView(presetView)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }


    }
}