package com.example.drinkitnowpare

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class DashboardActivity : AppCompatActivity() {

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_dashboard)

        val total_transaction = findViewById<TextView>(R.id.total_transaction)
        val total_amount = findViewById<TextView>(R.id.total_amount)

        val db = FirebaseFirestore.getInstance()
        val ordersCollection = db.collection("orders")

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val currentDateString = dateFormat.format(currentDate)

        val startOfDay = Timestamp(currentDate)
        val end = currentDate.time + 86400000
        val endOfDay = Timestamp(Date(end)) // 86400000 milliseconds = 24 hours

//        val btn_sales = findViewById<Button>(R.id.btn_sales)
//        val btn_task = findViewById<Button>(R.id.btn_task)
//
//        btn_sales.setOnClickListener(){
//            val intent = Intent(this,SalesReportActivity::class.java)
//            startActivity(intent)
//
//        }
//        btn_task.setOnClickListener(){
//            val intent = Intent(this,dashbord::class.java)
//            startActivity(intent)
//        }


        ordersCollection
            .whereGreaterThanOrEqualTo("timestamp", startOfDay)
            .whereLessThan("timestamp", endOfDay)
            .get()
            .addOnSuccessListener { documents ->
                var totalSum = 0.0
                var totalTransactions = 0
                for (document in documents) {
                    val total:Double = document.getDouble("total")!!
                    totalSum += total
                    totalTransactions += 1
                }
                total_transaction.text = totalTransactions.toString() + " Customers"
                // Format the totalSum to a string with two decimal places
                val formattedTotal = String.format("%.2f", totalSum)
                total_amount.text = "Php " + formattedTotal

            }
            .addOnFailureListener { exception ->

            }


        //Best Selling

        // Initialize Firestore
        val firestore = FirebaseFirestore.getInstance()

// Get the current date
        val currentDateTime = Calendar.getInstance()

// Calculate the start and end dates for the current month
        currentDateTime.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = currentDateTime.time
        currentDateTime.add(Calendar.MONTH, 1)
        currentDateTime.add(Calendar.DAY_OF_MONTH, -1)
        val endOfMonth = currentDateTime.time

// Query Firestore to get orders within the current month
        val query = firestore.collection("orders")
            .whereGreaterThanOrEqualTo("timestamp", startOfMonth)
            .whereLessThanOrEqualTo("timestamp", endOfMonth)

// Initialize a map to store product quantities
        val productQuantities = mutableMapOf<String, Long>()
        val productNames = mutableMapOf<String, String>()

        val scrollContainer: LinearLayout = findViewById(R.id.scrollContainer)

// Execute the query and aggregate product quantities
        query.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val products = document["products"] as List<Map<String, Any>>?
                    if (products != null) {
                        for (product in products) {
                            val productID = product["productID"] as String
                            val productName = product["productName"] as String
                            val quantity = product["quantity"] as Long

                            // Aggregate product quantities
                            productQuantities[productID] =
                                productQuantities.getOrDefault(productID, 0L) + quantity

                            // Store product name
                            productNames[productID] = productName
                        }
                    }
                }

                // Sort products by quantity in descending order
                val sortedProducts = productQuantities.entries.sortedByDescending { it.value }

                // Get the top 5 products
                val topProducts = sortedProducts.take(5)

                // Now, topProducts contains the top 5 products with the most combined quantity in the current month
                // You can use this list as needed
                // For example, you can print the top products:
                for ((index, entry) in topProducts.withIndex()) {
                    val productID = entry.key
                    val quantity = entry.value
                    val productName = productNames[productID]
//                    println("Top Product ${index + 1}:")
//                    println("Product ID: $productID")
//                    println("Quantity: $quantity")
//                    println()

                    val presetView: View = LayoutInflater.from(this@DashboardActivity).inflate(R.layout.best_selling_item, null)
                    val presetTitle: TextView = presetView.findViewById(R.id.productNameTextView)
                    val params =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    params.setMargins(0, 0, 20, 0);
                    presetView.layoutParams = params
                    presetTitle.text = productName
                    scrollContainer.addView(presetView)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }

        val drawerLayout = findViewById<DrawerLayout>(R.id.my_drawer_layout);
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this,DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.task -> {
                    val intent = Intent(this,dashbord::class.java)
                    startActivity(intent)
                    true
                }
                R.id.sales -> {
                    val intent = Intent(this,SalesReportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    Firebase.auth.signOut()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}