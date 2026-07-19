package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OtherUserProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        userId = intent.getStringExtra("uid")
        if (userId == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        
        findViewById<View>(R.id.btnReport).setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("targetId", userId)
            intent.putExtra("targetType", "user")
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnFollow).setOnClickListener {
            Toast.makeText(this, "Following user", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnChat).setOnClickListener {
            val intent = Intent(this, ConversationActivity::class.java)
            intent.putExtra("otherUid", userId)
            startActivity(intent)
        }

        loadUserData()
    }

    private fun loadUserData() {
        db.collection("users").document(userId!!).get().addOnSuccessListener { doc ->
            findViewById<TextView>(R.id.tvProfileName).text = doc.getString("name") ?: "User"
            findViewById<TextView>(R.id.tvBio).text = doc.getString("bio") ?: "No bio yet"
            findViewById<TextView>(R.id.tvProfileId).text = "ID: ${userId!!.take(7).uppercase()}"
        }
    }
}