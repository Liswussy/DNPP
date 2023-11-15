package com.example.drinkitnowpare

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ConsignorSalesReportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consi_sls_rpt)

        // Initialize Firestore
        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("products")

// Create a map to store unique suppliers and associated document IDs
        val supplierMap = mutableMapOf<String, MutableList<String>>()

        val linearLayout: LinearLayout = findViewById(R.id.layout) // Replace with your LinearLayout's ID

        // Initialize the LayoutInflater
        val inflater = LayoutInflater.from(this)


// Query the collection to get all documents
        collectionReference.get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                for (document in querySnapshot.documents) {
                    // Get the "supp" field value from each document
                    val supp = document.getString("supp")

                    // Check if the "supp" value is not null
                    if (supp != null) {
                        // If the supplier is not already in the map, add it
                        if (!supplierMap.containsKey(supp)) {
                            supplierMap[supp] = mutableListOf()
                        }

                        // Add the document ID to the list associated with the supplier
                        supplierMap[supp]?.add(document.id)
                    }
                }

                // Now, 'supplierMap' contains unique suppliers and associated document IDs
                for ((supp, documentIds) in supplierMap) {
                    println("Supplier: $supp, Document IDs: $documentIds")


                    val dpValue = 40 // Desired height in dp
                    val density =
                        resources.displayMetrics.density // Display density in dp per pixel
                    val heightInPixels = (dpValue * density).toInt() // Convert dp to pixels

                    val textView = TextView(this)
                    textView.text = supp
                    textView.width = LinearLayout.LayoutParams.MATCH_PARENT // Set width
                    textView.height = heightInPixels // Set height
                    textView.setTextColor(Color.parseColor("#1B21E8")) // Set text color
                    val textSizeInSp = 20 // Set the desired text size in scaled pixels
                    textView.textSize = textSizeInSp.toFloat()
                    textView.setTypeface(null, Typeface.BOLD)

                    // Set padding for spacing
                    val paddingLeft = 70
                    val paddingTop = 10
                    val paddingRight = 0
                    val paddingBottom = 10
                    textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

                    // Create a shape drawable for the outline
                    val shapeDrawable = GradientDrawable()
                    shapeDrawable.shape = GradientDrawable.RECTANGLE
                    shapeDrawable.setColor(Color.TRANSPARENT) // Set background color
                    shapeDrawable.setStroke(2, Color.BLACK) // Set border width and color

                    // Set the shape drawable as the background of the TextView
                    textView.background = shapeDrawable

                    // Set margins for spacing
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
//                    layoutParams.setMargins(0, 0, 0, 16) // Adjust the values as needed
//                    textView.layoutParams = layoutParams

                    textView.setOnClickListener { view ->
                        val intent = Intent(this, ConsignorProductDetailsActivity::class.java)
                        intent.putStringArrayListExtra("documentIds", ArrayList(documentIds))
                        startActivity(intent)
                    }

                    linearLayout.addView(textView)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }
}