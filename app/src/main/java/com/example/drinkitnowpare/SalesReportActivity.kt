package com.example.drinkitnowpare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class SalesReportActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sales_report)

        val imageView = findViewById<ImageView>(R.id.dateButton)

        imageView.setOnClickListener {
            showPopupMenu(it)
        }

        queryFirestore("today")


//        val firestore = FirebaseFirestore.getInstance()
//
//        val ordersCollection = firestore.collection("orders")
//
//        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

//        ordersCollection.get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    // Access each document's data here
//
//                }
//            }
//            .addOnFailureListener { exception ->
//                println("Error getting documents: $exception")
//            }


    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.sales_dropdown_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_today -> {
                    // Handle "Today" click
                    queryFirestore("today")
                    true
                }
                R.id.menu_yesterday -> {
                    // Handle "Yesterday" click
                    queryFirestore("yesterday")
                    true
                }
                R.id.menu_last_7_days -> {
                    // Handle "Last 7 days" click
                    queryFirestore("last_7_days")
                    true
                }
                R.id.menu_last_30_days -> {
                    // Handle "Last 30 days" click
                    queryFirestore("last_30_days")
                    true
                }
                R.id.menu_this_month -> {
                    // Handle "This Month" click
                    queryFirestore("this_month")
                    true
                }
                R.id.menu_last_month -> {
                    // Handle "Last Month" click
                    queryFirestore("last_month")
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun queryFirestore(timePeriod: String) {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        when (timePeriod) {
            "today" -> {
                // Query for documents created today
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val yesterday = calendar.time
                queryFirestoreWithTimestamp(yesterday, currentDate)
            }
            "yesterday" -> {
                // Query for documents created yesterday
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val yesterday = calendar.time
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                val dayBeforeYesterday = calendar.time
                queryFirestoreWithTimestamp(dayBeforeYesterday, yesterday)
            }
            "last_7_days" -> {
                // Query for documents created in the last 7 days
                calendar.add(Calendar.DAY_OF_MONTH, -7)
                val sevenDaysAgo = calendar.time
                queryFirestoreWithTimestamp(sevenDaysAgo, currentDate)
            }
            "last_30_days" -> {
                // Query for documents created in the last 30 days
                calendar.add(Calendar.DAY_OF_MONTH, -30)
                val thirtyDaysAgo = calendar.time
                queryFirestoreWithTimestamp(thirtyDaysAgo, currentDate)
            }
            "this_month" -> {
                // Query for documents created in the current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val firstDayOfMonth = calendar.time
                queryFirestoreWithTimestamp(firstDayOfMonth, currentDate)
            }
            "last_month" -> {
                // Query for documents created in the last month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.MONTH, -1)
                val firstDayOfLastMonth = calendar.time
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val lastDayOfLastMonth = calendar.time
                queryFirestoreWithTimestamp(firstDayOfLastMonth, lastDayOfLastMonth)
            }
        }
    }

    private fun queryFirestoreWithTimestamp(startDate: Date, endDate: Date) {
        val db = Firebase.firestore

        db.collection("orders")
            .whereGreaterThanOrEqualTo("timestamp", startDate)
            .whereLessThanOrEqualTo("timestamp", endDate)
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    val scrollContainer: LinearLayout = findViewById(R.id.scrollContainer)
                    scrollContainer.removeAllViews()

                    var totalSales:Double = 0.0

                    for (document in querySnapshot){
                        val documentData = document.data
                        val timestamp = document.getTimestamp("timestamp")
                        val total = document.getDouble("total")
                        val products = document.get("products") as List<Map<String, Any>>?

                        if (total != null) {
                            totalSales += total
                        }

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

                    val totalSalesTextView = findViewById<TextView>(R.id.totalSales)
                    val netSales = findViewById<TextView>(R.id.netSales)

                    totalSalesTextView.text = "Total Sales: Php: $totalSales"
                    netSales.text = "Net Sales: Php: $totalSales" //change this probably


                } else {
                    // Handle errors
                }
            }
    }
}