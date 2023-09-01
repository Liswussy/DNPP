package com.example.drinkitnowpare

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class CashRegisterActivity : ComponentActivity() {

    private lateinit var scanButton: ImageButton
    private val CAMERA_PERMISSION_REQUEST_CODE = 101

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startBarcodeScanner()
            } else {
                // Handle permission denied
            }
        }

    val productManager = ProductManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_register)

        val productList = intent.getSerializableExtra("productList") as? ArrayList<Product>

        val parentLinearLayout: LinearLayout = findViewById(R.id.linearLayout)



        if (productList != null) {
            productManager.setProductList(productList)
        }

        fun showAllProducts() {
            val db = Firebase.firestore
            val linearLayout = findViewById<LinearLayout>(R.id.view)

            val productsCollection = db.collection("products")

            val dpValue = 40 // Desired height in dp
            val density = resources.displayMetrics.density // Display density in dp per pixel
            val heightInPixels = (dpValue * density).toInt() // Convert dp to pixels

            productsCollection.get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val productID = document.id
                        val productName = document.getString("prdnme")
                        val price = document.getDouble("price")

                        val horizontalLayout = createHorizontalLinearLayout(this)
                        val verticalLayout = createVerticalLinearLayout(this)

                        val imageView = createImageView()
                        val textView = createTextView(productName.toString())
                        val textView2 = createTextView("Php " + price.toString())

                        // Add ImageView and TextView to the ConstraintLayout
                        horizontalLayout.addView(imageView)

                        verticalLayout.addView(textView)
                        verticalLayout.addView(textView2)
                        horizontalLayout.addView(verticalLayout)

                        val layoutParams =
                            horizontalLayout.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.setMargins(
                            layoutParams.leftMargin,
                            layoutParams.topMargin,
                            layoutParams.rightMargin,
                            resources.getDimensionPixelSize(R.dimen.bottom_margin) // Replace with your desired margin value
                        )

                        horizontalLayout.setOnClickListener {
                            // Handle the click event here
                            // For example, you can show a Toast message
                            val newProduct = Product(productID, productName, price, 1)
                            productManager.addProduct(newProduct)
                        }

                        // Add ConstraintLayout to the parent LinearLayout
                        parentLinearLayout.addView(horizontalLayout)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }

        showAllProducts()

        val buttonPayment = findViewById<Button>(R.id.buttonPayment)
        buttonPayment.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("productList", ArrayList(productManager.getProductList()))
            startActivity(intent)
        }

        scanButton = findViewById(R.id.scanButton)
        scanButton.setOnClickListener {
            if (checkCameraPermission()) {
                startBarcodeScanner()
            } else {
                requestCameraPermission()
            }
        }

    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            // You can show an explanation here if needed
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startBarcodeScanner() {
        val integrator = IntentIntegrator(this@CashRegisterActivity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
        integrator.setPrompt("Scan a Barcode")
        integrator.setCameraId(0) // Use the rear-facing camera
        integrator.initiateScan()
    }

    // Override onActivityResult to handle the scanned barcode result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                // Handle canceled scan
            } else {
                // Handle successful scan
                val barcodeContents = result.contents
                getProduct(barcodeContents)

                // Do something with the barcode data (e.g., display it)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBarcodeScanner()
            } else {
                // Handle permission denied
            }
        }
    }

    fun getProduct(productID: String) {
        val db = Firebase.firestore
        val docRef = db.collection("products").document(productID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.v(TAG, document.toString());
                    val productName = document.getString("prdnme")
                    val price = document.getDouble("price")

                    val newProduct = Product(document.id, productName, price, 1)
                    productManager.addProduct(newProduct)

                } else {
                    showToast("Product does not exist")

                }
            }
            .addOnFailureListener { exception ->
                showToast("Database error")

            }
    }

    private fun createHorizontalLinearLayout(context: Context): LinearLayout {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val horizontalLayout = LinearLayout(context)
        horizontalLayout.layoutParams = layoutParams
        horizontalLayout.orientation = LinearLayout.HORIZONTAL
        horizontalLayout.setBackgroundColor(0xFFA6A6A6.toInt()) // Set background color

        return horizontalLayout
    }

    private fun createVerticalLinearLayout(context: Context): LinearLayout {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val verticalLayout = LinearLayout(context)
        verticalLayout.layoutParams = layoutParams
        verticalLayout.orientation = LinearLayout.VERTICAL

        return verticalLayout
    }

    private fun createImageView(): ImageView {
        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.ic_launcher_foreground) // Set your image resource here
        imageView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        return imageView
    }

    private fun createTextView(text: String): TextView {
        val textView = TextView(this)
        textView.text = text
        textView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        return textView
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

data class Product(
    val productID: String?,
    val productName: String?,
    val price: Double?,
    var quantity: Int?
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(productID)
        parcel.writeString(productName)
        parcel.writeValue(price)
        parcel.writeValue(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }

}

class ProductManager(private val context: Context) {
    private var productList = mutableListOf<Product>()

    fun addProduct(product: Product) {
        // Check if the product entry already exists
        val existingProduct = productList.find { it.productID == product.productID }

        if (existingProduct == null) {
            productList.add(product)
            showToast("Product added: ${product.productName}", 500)
        } else {
            showToast("Product with ID ${product.productID} already exists.", 500)
        }
    }

    fun getProductList(): MutableList<Product> {
        return productList
    }

    fun setProductList(array: ArrayList<Product>) {
        productList = array
        return
    }

    private fun showToast(message: String, durationMillis: Int) {
        val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        toast.duration = durationMillis
        toast.show()
    }
}