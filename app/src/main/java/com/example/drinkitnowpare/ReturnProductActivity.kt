package com.example.drinkitnowpare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.drinkitnowpare.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ReturnProductActivity : ComponentActivity() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_product)


        val defectSpinner = findViewById<Spinner>(R.id.defect_spinner)
        val supplierSpinner = findViewById<Spinner>(R.id.supp_spinner)
        val linearLayout = findViewById<LinearLayout>(R.id.linearlayout2)

        // Create a list of defects
        val defectList = listOf("Damage", "Expired", "Other Defects")

        // Populate the Spinner with defects
        val defectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, defectList)
        defectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        defectSpinner.adapter = defectAdapter

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



        val db = FirebaseFirestore.getInstance()
        val suppliers = mutableListOf<Map<String, Any?>>()

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
                val suppName = findViewById<Spinner>(R.id.supp_spinner)
                val secondAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, suppliersNames)
                secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                suppName.adapter = secondAdapter

                suppliersNames.add(0, "Select Supplier")

                // update products list whenever spinner value changes
                suppName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                                    for (prod in foundProducts) {

                                        val inflater = LayoutInflater.from(this@ReturnProductActivity)
                                        val itemView = inflater.inflate(R.layout.return_items, null)
                                        val textView = itemView.findViewById<TextView>(R.id.prd_name)
                                        val editTextView =
                                            itemView.findViewById<EditText>(R.id.prd_quanity)
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
                        linearLayout.removeAllViews()
                    }
                }
                val backArrorw = findViewById<ImageView>(R.id.btn_back)
                backArrorw.setOnClickListener{

                    val intent = Intent(this@ReturnProductActivity, ViewSuppliersActivity::class.java)
                    startActivity(intent)
                }


                val btnSave = findViewById<Button>(R.id.btn_save)
                btnSave.setOnClickListener {

                    val damageText = findViewById<Spinner>(R.id.defect_spinner).selectedItem.toString()

                    if (suppName.selectedItem == null || linearLayout.childCount == 0 || damageText == "Select Defect") {
                        // Display a message to the user indicating that required fields are empty
                        Toast.makeText(applicationContext, "Please fill in all required fields", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }

                    val editTextsInLinearLayouts = getAllEditTextsInLinearLayouts(linearLayout)

                    val productQuantities = mutableListOf<Map<String, Any>>()

                    for (editText in editTextsInLinearLayouts) {
                        val text = editText.text.toString()
                        val intValue = if (text.isBlank()) 0 else text.toIntOrNull() ?: 0
                        val prodID = editText.tag

                        productQuantities.add(mapOf("prodID" to prodID, "qty" to intValue))
                    }

                    // Initialize Firestore
                    val db = FirebaseFirestore.getInstance()
                    val returnReportsCollection = db.collection("return_report")

                    // Create a new document with the specified fields
                    val returnReport = hashMapOf(
                        "supplier" to suppName.selectedItem.toString(),
                        "products" to productQuantities,
                        "damage" to damageText,
                        "date" to FieldValue.serverTimestamp() // Use server timestamp for the date
                    )

                    // Add the document to the "return_report" collection
                    returnReportsCollection.add(returnReport)
                        .addOnSuccessListener { documentReference ->
                            Log.d("ReturnProductActivity", "Return report added with ID: ${documentReference.id}")
                            Toast.makeText(applicationContext, "Return Report Created", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("ReturnProductActivity", "Error adding return report: $e")
                            Toast.makeText(applicationContext, "Error adding return report", Toast.LENGTH_SHORT).show()
                        }

                    // Clear the selected item in the supplier spinner
                    suppName.setSelection(0)



                    // Clear the selected item in the defect spinner
                    defectSpinner.setSelection(0)

                    // Clear the linear layout containing dynamic views
                    linearLayout.removeAllViews()

                    // Clear the text in EditText fields
                    for (editText in getAllEditTextsInLinearLayouts(linearLayout)) {
                        editText.text.clear()
                    }

                    // Display a toast message indicating successful data submission
                    Toast.makeText(applicationContext, "Data submitted successfully", Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { exception ->
                // Handle any errors here
                println("Error getting documents: $exception")
            }
    }

    private fun getAllEditTextsInLinearLayouts(parentLinearLayout: LinearLayout): List<EditText> {
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
}