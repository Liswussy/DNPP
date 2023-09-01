package com.example.drinkitnowpare

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    public override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        auth.addAuthStateListener(authStateListener);
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, SalesReportActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val redirectButton = findViewById<Button>(R.id.submit)
        redirectButton.setOnClickListener {
            redirectToManageProduct()

        }

    }
    fun redirectToManageProduct() {

        val userName = findViewById<EditText>(R.id.username);
        val userPassword= findViewById<EditText>(R.id.password);

        val email = userName.text.toString();
        val password = userPassword.text.toString();

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(
                        baseContext,
                        "Login Success!",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val user = auth.currentUser
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth: FirebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
            // User is signed in
            // Perform actions when user is signed in
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is signed out
            // Perform actions when user is signed out
        }
    }

}

