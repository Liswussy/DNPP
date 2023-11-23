package com.example.drinkitnowpare

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditActivity : ComponentActivity() {

    private val supplierList = ArrayList<String>()
    private val supplierListID = ArrayList<String>()
    private lateinit var supplierAdapter: ArrayAdapter<String>

    private lateinit var supplierID: String
    private lateinit var productName: String

    private lateinit var prdnme: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)


        val db = Firebase.firestore

        val receivedData = intent.getStringExtra("prodID")

        // Set product values
        val docRef = db.collection("products").document(receivedData.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    val prdname = findViewById<EditText>(R.id.prdname)
                    prdname.setText(document.getString("prdnme"))
                    prdnme = document.getString("prdnme").toString()

                    val category = findViewById<Spinner>(R.id.categorySpinner)
                    val spinnerItems = listOf("Imported Beer", "Local Beer", "Gin", "Spirits")
                    val valueToSelect = document.getString("ctg")

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        spinnerItems
                    )
                    category.adapter = adapter

                    // Get the index of the value to select
                    val selectedIndex = spinnerItems.indexOf(valueToSelect)

                    // Set the selected item by index
                    if (selectedIndex != -1) {
                        category.setSelection(selectedIndex)
                    }

                    val supplier = findViewById<Spinner>(R.id.prd_sup)
                    // Remove the line setting text for Spinner as it's not applicable
                    // supplier.setText(document.getString("supp"))
                    // Now, update the supplier data dynamically
                    updateSupplierData()

                    val sku = findViewById<EditText>(R.id.prd_sku)
                    sku.setText(document.getString("sku"))
                    val quantity = findViewById<EditText>(R.id.prd_qty)
                    quantity.setText(document.getDouble("qnty")?.toInt().toString())
                    val cost = findViewById<EditText>(R.id.pt_cost)
                    cost.setText(document.getDouble("cost").toString())
                    val size = findViewById<EditText>(R.id.prd_size)
                    size.setText(document.getDouble("size").toString())

                    val units = findViewById<Spinner>(R.id.sizeoption)
                    val spinnerItems2 = listOf("Volume", "ml", "Lt")
                    val valueToSelect2 = document.getString("units")

                    val adapter2 = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        spinnerItems2
                    )
                    units.adapter = adapter2

                    // Get the index of the value to select
                    val selectedIndex2 = spinnerItems.indexOf(valueToSelect2)

                    // Set the selected item by index
                    if (selectedIndex2 != -1) {
                        category.setSelection(selectedIndex2)
                    }

                    val price = findViewById<EditText>(R.id.prd_price)
                    price.setText(document.getDouble("price").toString())

                    supplierID = document.getString("supplierID").toString()
                    productName = document.getString("prdnme").toString()

                } else {
                    Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error retrieving data", Toast.LENGTH_SHORT).show()
            }


        // Update product
        val btn_update = findViewById<Button>(R.id.btn_update)
        val prodRef = db.collection("products").document(receivedData.toString())
        btn_update.setOnClickListener {
            //get the data
            val prdname = findViewById<EditText>(R.id.prdname).text.toString()
            val category = findViewById<Spinner>(R.id.categorySpinner).selectedItem.toString()
            val supplier = findViewById<Spinner>(R.id.prd_sup).selectedItem.toString()
            val sku = findViewById<EditText>(R.id.prd_sku).text.toString()
            val cost = findViewById<EditText>(R.id.pt_cost).text.toString()
            val quantity = findViewById<EditText>(R.id.prd_qty).text.toString()
            val size = findViewById<EditText>(R.id.prd_size).text.toString()
            val units = findViewById<Spinner>(R.id.sizeoption).selectedItem.toString()
            val price = findViewById<EditText>(R.id.prd_price).text.toString()
            val supplierID = supplierListID[supplierList.indexOf(supplier)]

            // Update product data
            prodRef
                .update(
                    "prdnme", prdname,
                    "ctg", category,
                    "supp", supplier,
                    "sku", sku,
                    "qnty", quantity.toInt(),
                    "cost", cost.toDouble(),
                    "size", size.toDouble(),
                    "units", units,
                    "price", price.toDouble(),
                    "supplierID", supplierID
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Successfully updated product", Toast.LENGTH_SHORT).show()

                    // If you want to update supplier data, add the following lines
                    val supplierId =
                        supplierListID[findViewById<Spinner>(R.id.prd_sup).selectedItemPosition]
                    val supplierRef = db.collection("suppliers").document(supplierId)

                    val oldProduct = mapOf(
                        "id" to receivedData.toString(),
                        "name" to prdnme.toString()
                    )

                    println(oldProduct.toString())

                    // Create a map with the updated product name
                    val updatedProduct = mapOf(
                        "id" to receivedData.toString(),
                        "name" to prdname
                    )

                    // Update the product field in the supplier collection
                    supplierRef
                        .update(
                            "products", FieldValue.arrayRemove(oldProduct),
                            "products", FieldValue.arrayUnion(updatedProduct),
                            // Add other fields to update in the supplier collection if needed
                            // For example, if you have a field called "lastUpdated", you can set it to the current timestamp
                            "lastUpdated", System.currentTimeMillis()
                        )
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Successfully updated supplier",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error updating supplier: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating product: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        val btn_delete = findViewById<Button>(R.id.btn_delete)
        btn_delete.setOnClickListener {
            val receivedData = intent.getStringExtra("prodID")
            deleteProductAndReferences(receivedData.toString(), supplierID, productName)
        }
    }

    //delete product

    private fun deleteProductAndReferences(
        productID: String,
        supplierID: String,
        productName: String
    ) {
        val db = Firebase.firestore

        // Delete the product from the product collection
        val productRef = db.collection("products").document(productID)
        productRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val productName = documentSnapshot.getString("prdnme")

                    // Delete the product document
                    productRef.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product successfully deleted", Toast.LENGTH_SHORT).show()

                            // Remove references from the supplier collection
                            val washingtonRef = db.collection("suppliers").document(supplierID)
                            val data = mapOf(
                                "id" to productID,
                                "name" to productName
                            )
                            washingtonRef.update("products", FieldValue.arrayRemove(data))
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error deleting product: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error retrieving product: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // Function to update the supplier data in the spinner dynamically
    private fun updateSupplierData() {
        // Retrieve the list of suppliers from Firestore
        val db = Firebase.firestore
        val supplierCollectionRef = db.collection("suppliers")

        // Initialize the adapter if it is not initialized
        if (!::supplierAdapter.isInitialized) {
            supplierAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, supplierList)
            supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Assuming you have a Spinner with the id prd_sup
            val supplierSpinner = findViewById<Spinner>(R.id.prd_sup)
            supplierSpinner.adapter = supplierAdapter
        }

        supplierCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                // Clear the existing data in the supplierList and supplierListID
                supplierList.clear()
                supplierListID.clear()

                for (document in querySnapshot) {
                    // Add each supplier's name to the list
                    val supplierName = document.getString("name")
                    if (supplierName != null) {
                        supplierList.add(supplierName)
                        supplierListID.add(document.id)
                    }
                }

                // Add "Add Supplier" option at the top
                supplierList.add(0, "Add Supplier")
                supplierListID.add(0, "Add Supplier")

                // Notify the adapter that the data has changed
                supplierAdapter.notifyDataSetChanged()

                // Now, set the selected item based on the actual supplier of the product
                val receivedData = intent.getStringExtra("prodID")
                val docRef = db.collection("products").document(receivedData.toString())
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val actualSupplier = document.getString("supp")

                            // Check if the actual supplier is in the list
                            val selectedIndex = supplierList.indexOf(actualSupplier)
                            if (selectedIndex != -1) {
                                val supplierSpinner = findViewById<Spinner>(R.id.prd_sup)
                                supplierSpinner.setSelection(selectedIndex)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error retrieving product data", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to retrieve suppliers", Toast.LENGTH_SHORT).show()
            }
    }


}





