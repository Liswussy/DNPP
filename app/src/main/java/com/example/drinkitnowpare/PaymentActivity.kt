package com.example.drinkitnowpare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class PaymentActivity : ComponentActivity() {

    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val productList = intent.getSerializableExtra("productList") as? ArrayList<Product>

        var total = 0.00

        fun calculateTotal() {
            total = 0.00
            if (productList != null) {
                if (productList.isEmpty()) {
                    val textViewSubTotal = findViewById<TextView>(R.id.textViewSubTotal)
                    textViewSubTotal.text = "P $total"
                    val textViewTotal = findViewById<TextView>(R.id.textViewTotal)
                    textViewTotal.text = "P $total"
                }

                for (product in productList) {
                    val price = product.price
                    val quantity = product.quantity
                    total += price!! * quantity!!
                    val textViewSubTotal = findViewById<TextView>(R.id.textViewSubTotal)
                    textViewSubTotal.text = "P $total"
                    val textViewTotal = findViewById<TextView>(R.id.textViewTotal)
                    textViewTotal.text = "P $total"
                }
            } else {
                println("Product list is null.")
            }
        }

        calculateTotal()

        val linearLayout = findViewById<LinearLayout>(R.id.cartList)

        if (productList != null) {
            for (product in productList) {
                val id = product.productID
                val name = product.productName
                val price = product.price
                val quantity = product.quantity
                val quantityText = "Quantity: " + quantity.toString()

                val entryView = layoutInflater.inflate(R.layout.entry_layout, null)

                val productNameTextView = entryView.findViewById<TextView>(R.id.productNameTextView)
                val productPriceTextView = entryView.findViewById<TextView>(R.id.productPriceTextView)
                val productQuantityTextView = entryView.findViewById<TextView>(R.id.productQuantityTextView)
                val subButton = entryView.findViewById<Button>(R.id.subButton)
                val addButton = entryView.findViewById<Button>(R.id.addButton)
                val modifyButton = entryView.findViewById<Button>(R.id.modifyButton)

                productNameTextView.text = name
                productPriceTextView.text = price.toString()
                productQuantityTextView.text = quantityText

                subButton.setOnClickListener() {
                    for (product in productList) {
                        if (product.productID == id) {
                            product.quantity = product.quantity?.plus(-1)
                            val quantityText = "Quantity: " + product.quantity.toString()
                            productQuantityTextView.text = quantityText

                            if (product.quantity!! <= 0) {
                                linearLayout.removeView(entryView)
                                productList.removeIf { product -> product.productID == id }
                            }

                            calculateTotal()
                            break
                        }
                    }
                }

                addButton.setOnClickListener() {
                    for (product in productList) {
                        if (product.productID == id) {
                            product.quantity = product.quantity?.plus(1)
                            val quantityText = "Quantity: " + product.quantity.toString()
                            productQuantityTextView.text = quantityText

                            calculateTotal()
                            break
                        }
                    }
                }

                modifyButton.setOnClickListener() {
                    val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)
                    val leftTextView: TextView = dialogView.findViewById(R.id.leftTextView)
                    val rightTextView: TextView = dialogView.findViewById(R.id.rightTextView)
                    val confirmButton: Button = dialogView.findViewById(R.id.confirmButton)
                    val editTextNumber: EditText = dialogView.findViewById(R.id.editTextNumber)

                    if (quantity != null) {
                        editTextNumber.setText(product.quantity.toString())
                    }

                    leftTextView.text = name

                    val dialog = AlertDialog.Builder(this)
                        .setView(dialogView)
                        .create()

                    confirmButton.setOnClickListener {
                        for (product in productList) {
                            if (product.productID == id) {
                                val inputText = editTextNumber.text.toString()
                                if (inputText.isNotEmpty()) {
                                    val numericValue = inputText.toInt()
                                    product.quantity = numericValue
                                    val quantityText = "Quantity: " + product.quantity.toString()
                                    productQuantityTextView.text = quantityText

                                    if (product.quantity!! <= 0) {
                                        linearLayout.removeView(entryView)
                                        productList.removeIf { product -> product.productID == id }
                                    }

                                    calculateTotal()
                                    break
                                } else {
                                    println("EditText is empty")
                                }
                            }
                        }
                        dialog.dismiss()
                    }

                    dialog.show()
                }

                linearLayout.addView(entryView)
            }
        }

        val buttonPayment = findViewById<Button>(R.id.button)
        buttonPayment.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("productList", productList)
            intent.putExtra("total", total)
            startActivity(intent)
        }

        val myTextView = findViewById<TextView>(R.id.textView6)
        val text = "Add more items"
        val content = SpannableString(text)
        content.setSpan(UnderlineSpan(), 0, text.length, 0)
        myTextView.text = content
        myTextView.setOnClickListener() {
            val intent = Intent(this, CashRegisterActivity::class.java)
            intent.putExtra("productList", productList)
            startActivity(intent)
        }

        // Firestore update code is added here

        val discountSpinner = findViewById<Spinner>(R.id.discount_spinner)
        val discountOption = listOf("5%", "10%", "15%", "20%", "30%", "40%", "50%")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, discountOption)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        discountSpinner.adapter = adapter

        discountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                if (selectedItemView != null) {
                    println("$position")
                    val selectedDiscount = discountOption[position].replace("%", "").toDouble()
                    println("$selectedDiscount")

                    val discountedAmount = total * (selectedDiscount / 100)
                    val discountedTotal = total - discountedAmount
                    println("$discountedTotal")

                    runOnUiThread {
                        val textViewDiscountedTotal =
                            findViewById<TextView>(R.id.textViewDiscountedTotal)
                        textViewDiscountedTotal.text = "P $discountedTotal"
                    }

                    updateFirestoreDocument(selectedDiscount)
                } else {
                    println("selectedItemView is null")
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing if nothing is selected
            }
        }
    }

    private fun updateFirestoreDocument(selectedDiscount: Double) {
        val db = FirebaseFirestore.getInstance()
        val ordersCollection = db.collection("orders")

        if (orderId != null) {
            val orderDocument = ordersCollection.document(orderId!!)

            orderDocument.update("discount", selectedDiscount)
                .addOnSuccessListener {
                    println("Discount updated successfully")
                }
                .addOnFailureListener { e ->
                    println("Error updating discount: $e")
                }
        }
    }
}
