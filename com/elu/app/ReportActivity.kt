package com.elu.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReportActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var targetId: String? = null
    private var targetType: String? = null // "user" or "room"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        targetId = intent.getStringExtra("targetId")
        targetType = intent.getStringExtra("targetType")

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val rgReasons = findViewById<RadioGroup>(R.id.rgReasons)
        val etDetails = findViewById<EditText>(R.id.etDetails)
        val btnSubmit = findViewById<Button>(R.id.btnSubmitReport)

        btnSubmit.setOnClickListener {
            val selectedId = rgReasons.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reason = findViewById<RadioButton>(selectedId).text.toString()
            val details = etDetails.text.toString().trim()

            submitReport(reason, details)
        }
    }

    private fun submitReport(reason: String, details: String) {
        val myUid = auth.currentUser?.uid ?: return
        val report = hashMapOf(
            "reporterUid" to myUid,
            "targetId" to targetId,
            "targetType" to targetType,
            "reason" to reason,
            "details" to details,
            "status" to "pending",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("reports").add(report)
            .addOnSuccessListener {
                Toast.makeText(this, "Report submitted. Thank you for helping keep Elu safe.", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}