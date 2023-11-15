package com.example.drinkitnowpare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Calendar
import java.util.Date

class ConsignorProductDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consignor_prd_dtls)

        val datebtn = findViewById<Button>(R.id.datebutton)
        datebtn.setOnClickListener {
            showPopupMenu(it)
        }

        queryFirestore("today")


//        // Get the document IDs from the intent
//        val documentIds = intent.getStringArrayListExtra("documentIds")
//
//        // Initialize Firestore
//        val db = FirebaseFirestore.getInstance()
//        val ordersCollection = db.collection("orders")
//
//        // Initialize a variable to store the total cost
//        var totalCost = 0.0
//
//        // Query the "orders" collection to retrieve documents that match the criteria
//        if (documentIds != null) {
//            ordersCollection
//                .get()
//                .addOnSuccessListener { querySnapshot: QuerySnapshot ->
//                    for (document in querySnapshot.documents) {
//                        // Retrieve the "products" array from the document
//                        val products = document.get("products") as List<Map<String, Any>>
//
//                        println(products)
//
//                        // Iterate through the products
//                        for (product in products) {
//                            val productID = product["productID"] as String
//                            val price = product["price"] as Double
//                            val quantity = product["quantity"] as Long
//
//                            // Check if the productID matches one of the document IDs
//                            if (documentIds.contains(productID)) {
//                                // Calculate the total cost for this product and accumulate it
//                                val productTotal = price * quantity
//                                totalCost += productTotal
//
//                                // Get a reference to the LinearLayout in your layout
//                                val linearLayout = findViewById<LinearLayout>(R.id.itemLayout)
//
//                                // Create a TextView using LayoutInflater
//                                val inflater = LayoutInflater.from(this)
//                                val customComponent = inflater.inflate(R.layout.consignor_prd_item, null, false)
//
//                                val prdname = customComponent.findViewById<TextView>(R.id.tv_prd_name)
//                                val sold = customComponent.findViewById<TextView>(R.id.tv_sold)
//                                val amount = customComponent.findViewById<TextView>(R.id.tv_amount)
//
//                                // Set the text for the TextView
//                                prdname.text = productID
//                                sold.text = quantity.toString()
//                                amount.text = price.toString()
//
//                                // Add the TextView to the LinearLayout
//                                linearLayout.addView(customComponent)
//                            }
//                        }
//                    }
//
//                    // Now, 'totalCost' contains the sum of total costs for the matched products
//                    val tv_total_sold = findViewById<TextView>(R.id.tv_total_sold)
//                    println(totalCost)
//                    tv_total_sold.text = "P $totalCost"
//                }
//                .addOnFailureListener { exception ->
//                    // Handle errors here
//                }
//        }
//    }


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
                R.id.menu_this_year -> {
                    // Handle "Last Month" click
                    queryFirestore("this_year")
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
            "this_year" -> {
                // Query for documents created in the current year
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val firstDayOfThisYear = calendar.time
                val lastDayOfThisYear = Date() // Using the current date as the last day of the current year
                queryFirestoreWithTimestamp(firstDayOfThisYear, lastDayOfThisYear)
            }

        }
    }

    private fun queryFirestoreWithTimestamp(startDate: Date, endDate: Date) {
        // Get the document IDs from the intent
        val documentIds = intent.getStringArrayListExtra("documentIds")

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()

        // Initialize a variable to store the total cost
        var totalCost = 0.0

        // Query the "orders" collection to retrieve documents that match the criteria
        if (documentIds != null) {
            db.collection("orders")
                .whereGreaterThanOrEqualTo("timestamp", startDate)
                .whereLessThanOrEqualTo("timestamp", endDate)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
                .get()
                .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                    val linearLayout = findViewById<LinearLayout>(R.id.itemLayout)
                    linearLayout.removeAllViews()

                    for (document in querySnapshot.documents) {
                        // Retrieve the "products" array from the document
                        val products = document.get("products") as List<Map<String, Any>>


                        // Iterate through the products
                        for (product in products) {
                            val productID = product["productID"] as String
                            val productName = product["productName"] as String  // Change this line
                            val price = product["price"] as Double
                            val quantity = product["quantity"] as Long

                            // Check if the productID matches one of the document IDs
                            if (documentIds.contains(productID)) {
                                // Calculate the total cost for this product and accumulate it
                                val productTotal = price * quantity
                                totalCost += productTotal

                                // Create a TextView using LayoutInflater
                                val inflater = LayoutInflater.from(this)
                                val customComponent = inflater.inflate(R.layout.consignor_prd_item, null, false)

                                val prdname = customComponent.findViewById<TextView>(R.id.tv_prd_name)
                                val sold = customComponent.findViewById<TextView>(R.id.tv_sold)
                                val amount = customComponent.findViewById<TextView>(R.id.tv_amount)

                                // Set the text for the TextView using productName instead of productID
                                prdname.text = productName
                                sold.text = quantity.toString()
                                amount.text = price.toString()

                                // Add the TextView to the LinearLayout
                                linearLayout.addView(customComponent)
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