package com.elu.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FamilyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnCreateFamily).setOnClickListener {
            Toast.makeText(this, "Create Family feature coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnJoinFamily).setOnClickListener {
            Toast.makeText(this, "Join Family feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}