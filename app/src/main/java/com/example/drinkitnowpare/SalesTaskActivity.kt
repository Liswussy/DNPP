package com.example.drinkitnowpare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class SalesTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sales_task)

        val button_all_sales = findViewById<Button>(R.id.button_all_sales)
        button_all_sales.setOnClickListener{
            val intent = Intent(this, SalesReportActivity::class.java)
            startActivity(intent)
        }

        val btn_cons_sales = findViewById<Button>(R.id.btn_cons_sales)
        btn_cons_sales.setOnClickListener{
            val intent = Intent(this, ConsignorSalesReportActivity::class.java)
            startActivity(intent)
        }
    }
}