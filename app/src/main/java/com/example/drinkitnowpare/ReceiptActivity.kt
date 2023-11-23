package com.example.drinkitnowpare

import android.content.Intent
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.min
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.util.Date
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.FileOutputStream
import android.Manifest
import android.content.pm.PackageManager
import kotlin.math.max
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import android.view.View
import android.widget.*
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Rectangle
import java.util.*
import kotlin.math.min

class ReceiptActivity : ComponentActivity() {

    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1001
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

        val linearLayout =
            findViewById<LinearLayout>(R.id.itemcontainer) // Your main vertical LinearLayout

        subtotal.setText(totalstring)
        totalText.setText(totalstring)
        datercpt.setText(currentDate)

        if (productList != null) {
            for (product in productList) {
                val id = product.productID
                val name = product.productName
                val price = product.price
                val quantity = product.quantity

                val pricetext = "₱" + df.format(price)
                val amounttext = "₱" + df.format(price!! * quantity!!)

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
        // Calculate VAT and Vatable amounts
        val vatableAmount = total / 1.12
        val vat = vatableAmount * 0.12

        // Display VAT and Vatable amounts
        val vatableTextView = findViewById<TextView>(R.id.tv_vatabale)
        val vatTextView = findViewById<TextView>(R.id.vat)

        val vatableFormat = df.format(vatableAmount)
        val vatFormat = df.format(vat)

        vatableTextView.text = "₱$vatableFormat"
        vatTextView.text = "₱$vatFormat"

        val button_next = findViewById<Button>(R.id.btn_next)

        button_next.setOnClickListener {
            val intent = Intent(this, PaymentSuccessActivity::class.java)
            startActivity(intent)
            finish()

        }
        val downloadButton = findViewById<ImageButton>(R.id.imageButton)

        downloadButton.setOnClickListener {
            // Check for storage permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted, proceed with PDF creation and download
                generateAndDownloadPDF()
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE
                )
            }
        }

    }

    // Function to create a PDF file
    private fun createPDF(fileName: String, content: String) {
        val document = Document()
        val pdfFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        PdfWriter.getInstance(document, FileOutputStream(pdfFile))
        document.open()
        document.add(Paragraph(content))
        document.close()
    }


    // Function to generate and download PDF
    private fun generateAndDownloadPDF() {
        val pdfFileName = "Receipt_${System.currentTimeMillis()}.pdf"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val pdfFile = File(storageDir, pdfFileName)
        val pageSize = Rectangle(600f, 800f)
        val document = Document(pageSize)

        // Set a bold font for headers
        val headerFont = Font(Font.FontFamily.TIMES_ROMAN, 24f, Font.BOLD)

        // Set a bold font for other text
        val normalFont = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)

        try {
            PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            document.open()

            // Header
            val receiptContent = getReceiptContent()
            val headerParagraph = Paragraph(receiptContent, headerFont)
            headerParagraph.alignment = Element.ALIGN_CENTER // Center alignment
            document.add(headerParagraph)

            // Content
            val contentParagraph = Paragraph("", normalFont)
            contentParagraph.alignment = Element.ALIGN_CENTER // Center alignment
            document.add(contentParagraph)

            Toast.makeText(this, "PDF Downloaded Successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("PDF", "Error generating PDF: ${e.message}")
            e.printStackTrace()
        } finally {
            document.close()
        }
    }


//    private fun calculateFontSize(text: String, maxWidth: Float, maxHeight: Float, font: Font): Double {
//        val chunkWidth = font.getCalculatedBaseFont(true).getWidthPoint(text, font.size)
//        val chunkHeight = font.size
//
//        val widthScale = maxWidth / chunkWidth
//        val heightScale = maxHeight / chunkHeight
//
//        val scale = max(widthScale, heightScale) * 1.2 // Adjust the factor based on your preference
//
//        return scale * font.size
//    }

    // Function to retrieve the content of the receipt
    private fun getReceiptContent(): String {
        val receiptContentBuilder = StringBuilder()

        // Add a line to separate header
        receiptContentBuilder.append("------------------------------------------------\n")
        val headerTextView = findViewById<TextView>(R.id.textView19)
        val headerText = headerTextView.text.toString()

        // Append the modified text with the new font size and style to the receipt content
        receiptContentBuilder.append("$headerText\n")
        // Add a line to separate address
        receiptContentBuilder.append("------------------------------------------------\n")

        // Fetch additional header information
        val addressTextView = findViewById<TextView>(R.id.textView18)
        val addressText = addressTextView.text.toString()
        receiptContentBuilder.append("$addressText\n")

        // Add a line to separate customer information
        receiptContentBuilder.append("------------------------------------------------\n")

        // Fetch customer information
        val customerIdTextView = findViewById<TextView>(R.id.customer_id)
        val customerId = customerIdTextView.text.toString()
        receiptContentBuilder.append("Customer Name: $customerId\n")

        // Fetch date information
        val dateTextView = findViewById<TextView>(R.id.date_receipt)
        val dateText = dateTextView.text.toString()
        receiptContentBuilder.append("Date: $dateText\n")

        // Fetch SI number
        val siNumTextView = findViewById<TextView>(R.id.si_num)
        val siNum = siNumTextView.text.toString()
        receiptContentBuilder.append("S.I.#: $siNum\n")

        // Fetch cashier name
        val cashierNameTextView = findViewById<TextView>(R.id.cashier_name)
        val cashierName = cashierNameTextView.text.toString()
        receiptContentBuilder.append("Cashier Name: $cashierName\n")

        // Add a line to separate items
        receiptContentBuilder.append("------------------------------------------------\n")

        // Fetch items header
        receiptContentBuilder.append("Items\n")

        // Fetch product details
        val linearLayout = findViewById<LinearLayout>(R.id.itemcontainer)
        for (i in 0 until linearLayout.childCount) {
            val entryView = linearLayout.getChildAt(i)
            val productNameTextView = entryView.findViewById<TextView>(R.id.tv_prod_name)
            val productQuantityTextView = entryView.findViewById<TextView>(R.id.tv_qnty)
            val productPriceTextView = entryView.findViewById<TextView>(R.id.tv_price)
            val productAmountTextView = entryView.findViewById<TextView>(R.id.tv_amount)

            val productName = productNameTextView.text.toString()
            val quantity = productQuantityTextView.text.toString()
            val price = productPriceTextView.text.toString()
            val amount = productAmountTextView.text.toString()

            receiptContentBuilder.append("$productName - $quantity x $price = $amount\n")
        }

        // Add a line to separate totals
        receiptContentBuilder.append("------------------------------------------------\n")

        // Fetch subtotal, discount, vatable, VAT, and total
        val subtotalTextView = findViewById<TextView>(R.id.sub_total)
        val discountTextView = findViewById<TextView>(R.id.dicount)
        val vatableTextView = findViewById<TextView>(R.id.tv_vatabale)
        val vatTextView = findViewById<TextView>(R.id.vat)
        val totalTextView = findViewById<TextView>(R.id.total)

        val subtotal = subtotalTextView.text.toString()
        val discount = discountTextView.text.toString()
        val vatable = vatableTextView.text.toString()
        val vat = vatTextView.text.toString()
        val total = totalTextView.text.toString()

        receiptContentBuilder.append("Subtotal: $subtotal\n")
        receiptContentBuilder.append("Discount: $discount\n")
        receiptContentBuilder.append("Vatable: $vatable\n")
        receiptContentBuilder.append("VAT 12%: $vat\n")
        receiptContentBuilder.append("Total: $total\n")

        // Add a line to separate footer
        receiptContentBuilder.append("------------------------------------------------\n")

        return receiptContentBuilder.toString()
    }


    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with PDF creation and download
                generateAndDownloadPDF()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}