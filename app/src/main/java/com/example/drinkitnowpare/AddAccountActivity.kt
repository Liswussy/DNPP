package com.example.drinkitnowpare

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddAccountActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    val mAuth = FirebaseAuth.getInstance()

    lateinit var firstname:EditText
    lateinit var lastname:EditText
    lateinit var email:EditText
    lateinit var Password:EditText
    lateinit var passwordConfirm:EditText
    lateinit var contactnum:EditText
    lateinit var role:EditText

    lateinit var btn_create: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_account)

        firstname = findViewById<EditText>(R.id.firstname)
        lastname = findViewById<EditText>(R.id.lastname)
        email = findViewById<EditText>(R.id.email)
        Password = findViewById<EditText>(R.id.Password)
        passwordConfirm = findViewById<EditText>(R.id.passwordConfirm)
        contactnum = findViewById<EditText>(R.id.contactnum)
        role = findViewById<EditText>(R.id.role)

        btn_create = findViewById<Button>(R.id.btn_create)

        btn_create.setOnClickListener{
            if (Password.text.toString() == passwordConfirm.text.toString()){
                createUserAccount(email.text.toString(), Password.text.toString())
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createUserAccount(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    // User account created successfully
                    val user: FirebaseUser? = mAuth.currentUser
                    val db = Firebase.firestore

                    val data = hashMapOf(
                        "name" to "Tokyo",
                        "country" to "Japan",
                    )

                    val userData = hashMapOf(
                        "firstname" to firstname.text.toString(),
                        "lastname" to lastname.text.toString(),
                        "email" to email,
                        "contactnum" to contactnum.text.toString(),
                        "role" to role.text.toString()
                    )

                    if (user != null) {
                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener { Toast.makeText(this, "New Account Created", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(this, "Task failed", Toast.LENGTH_SHORT).show() }
                    }
                } else {
                    // User account creation failed
                    val exception = task.exception
                    // Handle the error, e.g., display an error message to the user
                }
            }
    }
}