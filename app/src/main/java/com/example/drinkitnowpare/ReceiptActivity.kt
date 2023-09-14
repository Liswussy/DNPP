package com.example.drinkitnowpare

import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.util.Date

class ReceiptActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.receipt_page)

        val productList = intent.getSerializableExtra("productList") as? ArrayList<Product>
        val total = intent.getDoubleExtra("total", 0.0)

        val df = DecimalFormat("#.##")

        val datercpt = findViewById<TextView>(R.id.date_receipt)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        val subtotal = findViewById<TextView>(R.id.sub_total)
        val totalText = findViewById<TextView>(R.id.total)
        val totalformat = df.format(total)
        val totalstring = "₱$totalformat"

        val linearLayout = findViewById<LinearLayout>(R.id.itemcontainer) // Your main vertical LinearLayout

        subtotal.setText(totalstring)
        totalText.setText(totalstring)
        datercpt.setText(currentDate)

        if (productList != null) {
            for(product in productList){
                val id = product.productID
                val name = product.productName
                val price = product.price
                val quantity = product.quantity

                val pricetext = "₱"+df.format(price)
                val amounttext = "₱"+df.format(price!! * quantity!!)

                val entryView = layoutInflater.inflate(R.layout.receipt_items, null)

                val productNameTextView = entryView.findViewById<TextView>(R.id.tv_prod_name)
                val productQuantityTextView = entryView.findViewById<TextView>(R.id.tv_qnty)
                val productPriceTextView = entryView.findViewById<TextView>(R.id.tv_price)
                val productAmountTextView = entryView.findViewById<TextView>(R.id.tv_amount)

                productNameTextView.text = name
                productPriceTextView.text = pricetext
                productQuantityTextView.text = quantity.toString()
                productAmountTextView.text = amounttext

                linearLayout.addView(entryView)
            }
        }
        val button_next = findViewById<Button>(R.id.btn_next)

        button_next.setOnClickListener{
            val intent = Intent(this, PaymentSuccessActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
}