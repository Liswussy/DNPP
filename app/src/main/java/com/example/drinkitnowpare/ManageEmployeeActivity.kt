package com.example.drinkitnowpare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class ManageEmployeeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_employee)

        // Add Account
        val btn_mnge_emp = findViewById<Button>(R.id.btn_addanother_acc)
        btn_mnge_emp.setOnClickListener{
            val intent = Intent(this, AddAccountActivity::class.java)
            startActivity(intent)
        }

        val linearLayout: LinearLayout = findViewById(R.id.layout) // Replace with your LinearLayout's ID

        // Initialize the LayoutInflater
        val inflater = LayoutInflater.from(this)

        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection.get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val firstName = document.getString("firstname") ?: ""
                    val lastName = document.getString("lastname") ?: ""
                    val email = document.getString("email") ?: ""
                    val contactNum = document.getString("contactnum") ?: ""
                    val role = document.getString("role") ?: ""

                    val itemView = inflater.inflate(R.layout.user_item, null)

                    val tv_firstname = itemView.findViewById<TextView>(R.id.tv_firstname)
                    val tv_lastname = itemView.findViewById<TextView>(R.id.tv_lastname)
                    val tv_email = itemView.findViewById<TextView>(R.id.tv_email)
                    val tv_contact = itemView.findViewById<TextView>(R.id.tv_contact)
                    val tv_role = itemView.findViewById<TextView>(R.id.tv_role)

                    tv_firstname.text = firstName
                    tv_lastname.text = lastName
                    tv_email.text = email
                    tv_contact.text = contactNum
                    tv_role.text = role

                    linearLayout.addView(itemView)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }

    }
}