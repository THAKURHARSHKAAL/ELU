package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnModQueue).setOnClickListener {
            startActivity(Intent(this, ReportQueueActivity::class.java))
        }

        findViewById<View>(R.id.btnUserManagement).setOnClickListener {
            startActivity(Intent(this, UserManagementActivity::class.java))
        }

        findViewById<View>(R.id.btnRoomMod).setOnClickListener {
            Toast.makeText(this, "Opening Room Moderation...", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnModLogs).setOnClickListener {
            Toast.makeText(this, "Opening Moderation Logs...", Toast.LENGTH_SHORT).show()
        }
    }
}