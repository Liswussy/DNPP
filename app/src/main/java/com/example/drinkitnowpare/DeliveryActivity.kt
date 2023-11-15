package com.example.drinkitnowpare

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import java.util.Date
import java.util.Locale

class DeliveryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delivery_form)

        val db = FirebaseFirestore.getInstance()
        val suppliers = mutableListOf<Map<String, Any?>>()

        val defectSpinner = findViewById<Spinner>(R.id.prd_defect)

        // Create a list of defects
        val defectList = listOf("Damage", "Expired", "Other Defects")

        // Populate the Spinner with defects
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, defectList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        defectSpinner.adapter = adapter

        // Handle Spinner item selection
        defectSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Handle item selection
                val selectedDefect = defectList[position]
                // Do something with the selected defect
                Log.d("SelectedDefect", "Defect: $selectedDefect")
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
            }
        }


        val suppliersCollection = db.collection("suppliers")
        suppliersCollection.get()
            .addOnSuccessListener { querySnapshot ->

                // List for spinner
                val suppliersNames = mutableListOf<String>()

                // iterate over all documents
                for (document in querySnapshot.documents) {
                    val name = document.getString("name")
                    if (name != null) {
                        suppliersNames.add(name)
                    }

                    val productsList = document.get("products") as List<Map<String, Any>>?
                    if (productsList != null) {
                        for (map in productsList) {
                            val key1 = map["id"]
                            val key2 = map["name"]
                            println("key1: $key1, key2: $key2")
                        }
                    }
                    val dataMap = mutableMapOf<String, Any?>()
                    name?.let { dataMap["name"] = it }
                    productsList?.let { dataMap["products"] = it }
                    suppliers.add(dataMap)
                }

                // fill spinner with supplier names
                val supp_name = findViewById<Spinner>(R.id.supp_sp)
                val secondAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, suppliersNames)
                secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                supp_name.adapter = secondAdapter

                // update products list whenever spinner value changes
                val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
                supp_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        // Handle item selection here
                        val selectedValue = parentView?.getItemAtPosition(position).toString()

                        linearLayout.removeAllViews()

                        for (dataMap in suppliers) {
                            val name = dataMap["name"] as? String
                            println(name)
                            println(selectedValue)
                            if (name == selectedValue) {
                                val foundProducts = dataMap["products"] as List<Map<String, Any>>?
                                println(foundProducts)
                                println("found")
                                if (foundProducts != null) {
                                    for (prod in foundProducts){

                                        val inflater = LayoutInflater.from(this@DeliveryActivity)
                                        val itemView = inflater.inflate(R.layout.delivery_form_item, null)
                                        val textView = itemView.findViewById<TextView>(R.id.prd_name)
                                        val editTextView = itemView.findViewById<EditText>(R.id.prd_quanity)
                                        textView.text = prod["name"] as CharSequence?
                                        editTextView.tag = prod["id"]
                                        linearLayout.addView(itemView)
                                    }
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parentView: AdapterView<*>?) {
                        // Handle nothing selected here
                    }
                }

                val btn_confirm = findViewById<Button>(R.id.btn_confirm)
                btn_confirm.setOnClickListener{

                    //val editTextValues = mutableListOf<Int>()
                    println("HELLO.")
                    val editTextsInLinearLayouts = getAllEditTextsInLinearLayouts(linearLayout)

                    val productQuantities = mutableListOf<Map<String, Any>>()

                    for (editText in editTextsInLinearLayouts) {
                        val text = editText.text.toString()
                        val intValue = if (text.isBlank()) 0 else text.toIntOrNull() ?: 0
                        val prodID = editText.tag

                        productQuantities.add(mapOf("prodID" to prodID, "qty" to intValue))

                        val prodRef = db.collection("products").document(prodID.toString())
                        prodRef.update("qnty", FieldValue.increment(intValue.toDouble()))
                            .addOnSuccessListener {
                                // Transaction was successful
                                println("Incremented field successfully.")

                            }.addOnFailureListener { e ->
                                // Transaction failed
                                println("Error incrementing field: $e")
                            }
                    }


                    val supplier = supp_name.selectedItem.toString()
                    val products = "Product Name" // Replace with the actual product name

                    val prdQuantityEditText = findViewById<EditText>(R.id.prd_quanity)

                    // Get the current date and time as a Firebase Timestamp
                    val currentDate = Timestamp.now()

                    // Get the quantity from the EditText and convert it to an Int
                    val quantity = prdQuantityEditText.text.toString().toIntOrNull() ?: 0

                    // Initialize Firestore
                    val db = FirebaseFirestore.getInstance()
                    val deliveryReportsCollection = db.collection("delivery_reports")

                    // Create a new document with the specified fields
                    val report = hashMapOf(
                        "supplier" to supplier,
                        "products" to productQuantities,
                        "date" to currentDate
                    )

                    // Add the document to the "delivery_reports" collection
                    deliveryReportsCollection.add(report)
                        .addOnSuccessListener { documentReference ->
                            // Document added successfully
                            val reportId = documentReference.id
                            println("Report added with ID: $reportId")
                            Toast.makeText(applicationContext, "Report Created", Toast.LENGTH_SHORT).show()
                            // You can perform any additional actions here, such as displaying a success message
                        }
                        .addOnFailureListener { e ->
                            // Handle errors here
                            println("Error adding report: $e")
                        }
                }

            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                println("Error getting documents: $exception")
            }


    }

    fun getAllEditTextsInLinearLayouts(parentLinearLayout: LinearLayout): List<EditText> {
        val editTexts = mutableListOf<EditText>()

        // Loop through all the child views of the parent LinearLayout
        for (i in 0 until parentLinearLayout.childCount) {
            val childView = parentLinearLayout.getChildAt(i)

            // Check if the child view is a LinearLayout
            if (childView is LinearLayout) {
                // Recursively call the function to get EditTexts in the child LinearLayout
                val editTextsInChild = getAllEditTextsInLinearLayouts(childView)
                editTexts.addAll(editTextsInChild)
            } else if (childView is EditText) {
                // Check if the child view is an EditText
                editTexts.add(childView)
            }
        }

        return editTexts
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
}