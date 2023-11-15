package com.example.drinkitnowpare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class ManagerTaskActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mng_prd_task)

        // Add New Product
        val btn_add_new_prd = findViewById<Button>(R.id.btn_add_new_prd)
        btn_add_new_prd.setOnClickListener{
            val intent = Intent(this, mngprd::class.java)
            startActivity(intent)
        }

        // Update Product
//        val btn_updt_product = findViewById<Button>(R.id.btn_updt_product)
//        btn_updt_product.setOnClickListener{
//            val intent = Intent(this, EditActivity ::class.java)
//            startActivity(intent)
//        }

        // Product Delivery
        val btn_prd_del = findViewById<Button>(R.id.btn_prd_del)
        btn_prd_del.setOnClickListener{
            val intent = Intent(this, DeliveryActivity::class.java)
            startActivity(intent)
        }
        //Add Supplier
        val btn_supp = findViewById<Button>(R.id.btn_supp)
        btn_supp.setOnClickListener{
            val intent = Intent(this, AddNewSupplierActivity::class.java)
            startActivity(intent)
        }

        // View Product
        val btn_view_task = findViewById<Button>(R.id.btn_view_task)
        btn_view_task.setOnClickListener{
            val intent = Intent(this, ViewDashboardActivity::class.java)
            startActivity(intent)
        }

        // Manage Employee
        val btn_mnge_emp = findViewById<Button>(R.id.btn_mnge_emp)
        btn_mnge_emp.setOnClickListener{
            val intent = Intent(this, ManageEmployeeActivity::class.java)
            startActivity(intent)
        }

        // Delivery Report
        val btn_del_rep = findViewById<Button>(R.id.btn_del_rep)
        btn_del_rep.setOnClickListener{
            val intent = Intent(this, delivery_report::class.java)
            startActivity(intent)
        }

        // Sales Report
        val btn_sales_report = findViewById<Button>(R.id.btn_sales_report)
        btn_sales_report.setOnClickListener{
            val intent = Intent(this, SalesTaskActivity::class.java)
            startActivity(intent)
        }
    }
}