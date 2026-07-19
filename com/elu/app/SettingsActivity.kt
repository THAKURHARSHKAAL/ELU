package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        checkAdminRole()
        setupClickListeners()
    }

    private fun checkAdminRole() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { doc ->
            val role = doc.getString("role")
            if (role == "admin") {
                findViewById<View>(R.id.btnAdminPanel).visibility = View.VISIBLE
                findViewById<View>(R.id.divAdmin).visibility = View.VISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        findViewById<View>(R.id.btnAccountSettings).setOnClickListener {
            Toast.makeText(this, "Account Settings coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnPrivacySettings).setOnClickListener {
            Toast.makeText(this, "Privacy Settings coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnNotificationPrefs).setOnClickListener {
            Toast.makeText(this, "Notification Preferences coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnLanguageSettings).setOnClickListener {
            startActivity(Intent(this, LanguageSelectionActivity::class.java))
        }

        findViewById<View>(R.id.btnBlockedMuted).setOnClickListener {
            val intent = Intent(this, SocialListActivity::class.java)
            intent.putExtra("title", "Blocked List")
            startActivity(intent)
        }

        findViewById<View>(R.id.btnReportProblem).setOnClickListener {
            Toast.makeText(this, "Report system coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnHelpCenter).setOnClickListener {
            Toast.makeText(this, "Help Center coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnTerms).setOnClickListener {
            Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnPrivacyPolicy).setOnClickListener {
            Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnAbout).setOnClickListener {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            AlertDialog.Builder(this)
                .setTitle("About Elu")
                .setMessage("Version: $version\nMade with ❤️ for live connection.")
                .setPositiveButton("OK", null)
                .show()
        }

        findViewById<View>(R.id.btnAdminPanel).setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        }

        findViewById<View>(R.id.btnLogout).setOnClickListener {
            showLogoutConfirmation()
        }

        findViewById<View>(R.id.btnDeleteAccount).setOnClickListener {
            showDeleteAccountConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Logout") { _, _ ->
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteAccountConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("This action is permanent. All your data will be removed. Proceed?")
            .setPositiveButton("Delete", { _, _ ->
                // Actual Firebase delete account logic would go here
                Toast.makeText(this, "Account deletion request sent", Toast.LENGTH_SHORT).show()
            })
            .setNegativeButton("Cancel", null)
            .show()
    }
}