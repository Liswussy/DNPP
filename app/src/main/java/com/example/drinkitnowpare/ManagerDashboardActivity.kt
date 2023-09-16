package com.example.drinkitnowpare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase

class ManagerDashboardActivity : AppCompatActivity() {
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mnger_dashboard)

        val drawerLayout = findViewById<DrawerLayout>(R.id.my_drawer_layout);
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this,DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.task -> {
                    val intent = Intent(this,ManagerTaskActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.sales -> {
                    val intent = Intent(this,SalesReportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    Firebase.auth.signOut()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }


        val db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("users")
        collectionReference.get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                val count = querySnapshot.size()
                val tv_active_employees = findViewById<TextView>(R.id.tv_active_employees)
                tv_active_employees.text = count.toString()
            }
            .addOnFailureListener { exception ->
                // Handle errors here
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}