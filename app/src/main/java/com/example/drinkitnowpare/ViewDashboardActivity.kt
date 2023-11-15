package com.example.drinkitnowpare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class ViewDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_dashboard)

        // View Product
        val btn_view_prd = findViewById<Button>(R.id.btn_view_prd)
        btn_view_prd.setOnClickListener {
            val intent = Intent(this, viewprd::class.java)
            startActivity(intent)

        }

        val btn_view_supp = findViewById<Button>(R.id.btn_view_supp)
        btn_view_supp.setOnClickListener {
            val intent = Intent(this, ViewSuppliersActivity::class.java)
            startActivity(intent)
        }

    }
}