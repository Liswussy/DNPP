package com.example.drinkitnowpare

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.*

import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionHistoryActivity : ComponentActivity() {

    private lateinit var myRoot: LinearLayout
    private lateinit var dateFilterSpinner: Spinner
    private val db = FirebaseFirestore.getInstance()
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back_btn)
        arrowBack.setOnClickListener {
            val intent = Intent(this, dashbord::class.java)
            startActivity(intent)
        }

        // Initialize UI elements
        myRoot = findViewById<View>(R.id.layoutOrders) as LinearLayout
        dateFilterSpinner = findViewById(R.id.dateFilterSpinner)

        // Set up the date filter spinner
        val dateFilterOptions = arrayOf("Select Date", "Today", "Yesterday", "7 Days Ago", "This Month", "This Year")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateFilterOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateFilterSpinner.adapter = adapter

        // Set the default selection to the first item ("Select Date Filter")
        dateFilterSpinner.setSelection(0)
        // Set a listener for item selection in the spinner
        dateFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                // Reload orders based on the selected date filter, skip if the default title is selected
                if (position != 0) {
                    showOrders()
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing here
            }
        }

        // Set up a daily reset timer
        val delay = calculateDelayUntilNextDay()
        handler.postDelayed({
            showOrders()
            handler.postDelayed(this::showOrders, 24 * 60 * 60 * 1000)
        }, delay.toLong())
    }

    private fun showOrders() {
        val currentDate = Calendar.getInstance().time
        val selectedFilter = dateFilterSpinner.selectedItem.toString()

        // Calculate start and end dates based on the selected filter
        val startDate = when (selectedFilter) {
            "Today" -> getStartOfDay(currentDate)
            "Yesterday" -> {
                val cal = Calendar.getInstance()
                cal.time = currentDate
                cal.add(Calendar.DAY_OF_MONTH, -1)
                getStartOfDay(cal.time)
            }
            "7 Days Ago" -> {
                val cal = Calendar.getInstance()
                cal.time = currentDate
                cal.add(Calendar.DAY_OF_MONTH, -7)
                getStartOfDay(cal.time)
            }
            "This Month" -> {
                val cal = Calendar.getInstance()
                cal.time = currentDate
                cal.set(Calendar.DAY_OF_MONTH, 1)
                getStartOfDay(cal.time)
            }
            "This Year" -> {
                val cal = Calendar.getInstance()
                cal.time = currentDate
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                getStartOfDay(cal.time)
            }
            else -> getStartOfDay(currentDate)
        }

        db.collection("orders")
            .whereGreaterThanOrEqualTo("timestamp", startDate)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                myRoot.removeAllViews() // Clear existing views
                for (document in querySnapshot.documents) {
                    val a = LinearLayout(this)
                    a.orientation = LinearLayout.HORIZONTAL
                    a.layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )

                    val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.US)

                    val view1 = TextView(this)
                    view1.text = " Customers"
                    view1.textSize = 18f
                    view1.setTextColor(Color.BLUE)
                    val params1 = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    view1.layoutParams = params1

                    val view2 = TextView(this)
                    view2.text = dateFormat.format(document.getTimestamp("timestamp")!!.toDate())
                    view2.textSize = 18f
                    view2.setTextColor(Color.parseColor("#EC3A3A"))
                    val params2 = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    view2.layoutParams = params2

                    val view3 = TextView(this)
                    view3.text = "Paid P" + document.getDouble("total").toString()
                    view3.textSize = 18f
                    view3.setTextColor(Color.BLUE)
                    val params3 = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    view3.layoutParams = params3

                    a.addView(view1)
                    a.addView(view2)
                    a.addView(view3)
                    myRoot.addView(a)
                }
            }
            .addOnFailureListener { e ->
                println("Error querying orders: $e")
            }
    }

    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    private fun calculateDelayUntilNextDay(): Int {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentSecond = calendar.get(Calendar.SECOND)

        // Calculate the time until the next day
        val millisecondsInDay = 24 * 60 * 60 * 1000
        val millisecondsUntilNextDay =
            millisecondsInDay - (currentHour * 60 * 60 * 1000 + currentMinute * 60 * 1000 + currentSecond * 1000)

        return millisecondsUntilNextDay
    }
}
