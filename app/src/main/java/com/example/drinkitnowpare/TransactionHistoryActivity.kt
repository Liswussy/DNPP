package com.example.drinkitnowpare

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


class TransactionHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back_btn)
        arrowBack.setOnClickListener{
            val intent = Intent(this, dashbord ::class.java)
            startActivity(intent)
        }

        val myRoot = findViewById<View>(R.id.layoutOrders) as LinearLayout

        val db = FirebaseFirestore.getInstance()

        fun showOrders(){
            db.collection("orders")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (document in querySnapshot.documents) {
                        val a = LinearLayout(this)
                        a.orientation = LinearLayout.HORIZONTAL
                        a.setLayoutParams(
                            LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
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

                        println("HELLO")

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
        showOrders()

    }
}