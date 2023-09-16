package com.example.drinkitnowpare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class dashbord : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashbord)

        val buttonManageOrders = findViewById<Button>(R.id.button_manageOrders)
        buttonManageOrders.setOnClickListener {
            val intent = Intent(this, ManageOrdersActivity::class.java)
            startActivity(intent)
        }

        // Manage Delivery
        val btn_mnge_emp = findViewById<Button>(R.id.button_manageDelivery)
        btn_mnge_emp.setOnClickListener{
            val intent = Intent(this, DeliveryActivity::class.java)
            startActivity(intent)
        }

    }
}