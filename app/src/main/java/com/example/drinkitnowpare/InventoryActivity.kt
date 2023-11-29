package com.example.drinkitnowpare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class InventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val btn_stocktake = findViewById<Button>(R.id.btn_stock_take)
        btn_stocktake.setOnClickListener {
            val intent = Intent(this, StockTakeActivity::class.java)
            startActivity(intent)
        }
    }
}