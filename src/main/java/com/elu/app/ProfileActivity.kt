package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnSignOut).setOnClickListener { signOut() }

        loadProfile()
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val name = doc.getString("name") ?: "Unnamed"
            val email = doc.getString("email") ?: ""
            val diamonds = doc.getLong("diamonds") ?: 0
            val points = doc.getLong("points") ?: 0

            findViewById<TextView>(R.id.tvProfileAvatar).text = name.take(1).uppercase()
            findViewById<TextView>(R.id.tvProfileName).text = name
            findViewById<TextView>(R.id.tvProfileEmail).text = email
            findViewById<TextView>(R.id.tvProfileDiamonds).text = diamonds.toString()
            findViewById<TextView>(R.id.tvProfilePoints).text = points.toString()
        }
    }

    private fun signOut() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
