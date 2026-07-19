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

        setupButtons()
        loadProfile()
    }

    private fun setupButtons() {
        findViewById<View>(R.id.btnEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<View>(R.id.btnFollowing).setOnClickListener {
            openSocialList("Following")
        }

        findViewById<View>(R.id.btnFollowers).setOnClickListener {
            openSocialList("Followers")
        }

        findViewById<View>(R.id.btnFriends).setOnClickListener {
            openSocialList("Friends")
        }

        findViewById<View>(R.id.btnVisitors).setOnClickListener {
            openSocialList("Profile Visitors")
        }

        findViewById<View>(R.id.btnBlocked).setOnClickListener {
            openSocialList("Blocked Users")
        }

        findViewById<View>(R.id.btnLevel).setOnClickListener {
            startActivity(Intent(this, LevelsBadgesActivity::class.java))
        }

        findViewById<View>(R.id.btnBadges).setOnClickListener {
            startActivity(Intent(this, LevelsBadgesActivity::class.java))
        }

        findViewById<View>(R.id.btnFamily).setOnClickListener {
            startActivity(Intent(this, FamilyActivity::class.java))
        }

        findViewById<View>(R.id.tvProfileDiamonds).setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }

        findViewById<View>(R.id.btnAchievements).setOnClickListener {
            Toast.makeText(this, "Achievements feature coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun openSocialList(title: String) {
        val intent = Intent(this, SocialListActivity::class.java)
        intent.putExtra("title", title)
        startActivity(intent)
    }

    private fun loadProfile() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val name = doc.getString("name") ?: "Unnamed"
            val diamonds = doc.getLong("diamonds") ?: 0
            val points = doc.getLong("points") ?: 0

            findViewById<TextView>(R.id.tvProfileName).text = name
            findViewById<TextView>(R.id.tvProfileId).text = "ID: ${uid.take(7).uppercase()}"
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
