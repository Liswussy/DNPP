package com.example.drinkitnowpare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CheckoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val db = Firebase.firestore

        val productList = intent.getSerializableExtra("productList") as? ArrayList<Product>
        val total = intent.getDoubleExtra("total", 0.0)

        var paymentMethod = "Cash"

        val textViewPaymentMethod: TextView = findViewById(R.id.textViewPaymentMethod)

        val textView9: TextView = findViewById(R.id.textView9)
        textView9.text = total.toString()

        val imageButton = findViewById<ImageButton>(R.id.imageButton)
        imageButton.setOnClickListener(){
            val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout_payment, null)
            val imageButtonGcash: ImageButton = dialogView.findViewById(R.id.imageButtonGcash)
            val imageButtonCash: ImageButton = dialogView.findViewById(R.id.imageButtonCash)
            val imageButtonBDO: ImageButton = dialogView.findViewById(R.id.imageButtonBDO)


            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            imageButtonGcash.setOnClickListener(){
                paymentMethod = "GCash"
                textViewPaymentMethod.text = paymentMethod
                dialog.dismiss()
            }

            imageButtonCash.setOnClickListener(){
                paymentMethod = "Cash"
                textViewPaymentMethod.text = paymentMethod
                dialog.dismiss()
            }

            imageButtonBDO.setOnClickListener(){
                paymentMethod = "BDO"
                textViewPaymentMethod.text = paymentMethod
                dialog.dismiss()
            }

            dialog.show()
        }

        val buttonConfirm = findViewById<Button>(R.id.button)
        buttonConfirm.setOnClickListener {

            val data = hashMapOf(
                "products" to productList,
                "total" to total,
                "timestamp" to com.google.firebase.Timestamp.now(),
                "paymentMethod" to paymentMethod
            )

            db.collection("orders")
                .add(data)
                .addOnSuccessListener {

                    if (productList != null) {
                        for (product in productList){
                            val productRef = db.collection("products").document(product.productID.toString())
                            val qty:Double = (product.quantity!! *-1).toDouble()
                            productRef.update("qnty", FieldValue.increment(qty))
                        }
                    }
                    val intent = Intent(this, ReceiptActivity::class.java)
                    intent.putExtra("productList", productList)
                    intent.putExtra("total", total)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    println("Error adding document: $e") }


        }
    }
}