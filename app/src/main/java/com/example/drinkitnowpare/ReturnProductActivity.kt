package com.example.drinkitnowpare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore

class ReturnProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_product)

        val defectSpinner = findViewById<Spinner>(R.id.defect_spinner)

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

        val db = FirebaseFirestore.getInstance()

        val supplierList = ArrayList<String>()

        // Initialize the spinner and adapter
        val supplierSpinner = findViewById<Spinner>(R.id.supp_spinner)
        val supplierAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, supplierList)
        supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        supplierSpinner.adapter = supplierAdapter

        // Add an "Add Supplier" option at the top of the spinner
        supplierList.add("Add Supplier")

        // Retrieve the list of suppliers from Firestore
        val supplierCollectionRef = db.collection("suppliers")
        supplierCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Add each supplier's name to the list
                    val supplierName = document.getString("name")
                    if (supplierName != null) {
                        supplierList.add(supplierName)
                    }
                }
                // Notify the adapter that the data has changed
                supplierAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(baseContext, "Failed to retrieve suppliers", Toast.LENGTH_SHORT).show()
            }

        // Set a listener to handle selection in the spinner
        supplierSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSupplier = supplierList[position]
                if (selectedSupplier != "Add Supplier") {
                    // If a supplier other than "Add Supplier" is selected, set the EditText to the selected supplier
                    // findViewById<EditText>(R.id.prd_sup).setText(selectedSupplier) // No need to set the EditText, it's handled by the spinner
                } else {
                    // Handle the "Add Supplier" option (e.g., open a new activity to add a supplier)
                    // You can add your custom logic here.
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
            }



        }

    }
}