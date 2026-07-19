package com.elu.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ReportQueueActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_queue)

        db = FirebaseFirestore.getInstance()
        rv = findViewById(R.id.rvReports)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        loadReports()
    }

    private fun loadReports() {
        db.collection("reports")
            .whereEqualTo("status", "pending")
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val reports = snapshot.documents
                rv.adapter = ReportAdapter(reports)
            }
    }

    inner class ReportAdapter(private val list: List<com.google.firebase.firestore.DocumentSnapshot>) : 
        RecyclerView.Adapter<ReportAdapter.VH>() {
        
        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val target = v.findViewById<TextView>(R.id.tvReportTarget)
            val reason = v.findViewById<TextView>(R.id.tvReportReason)
            val details = v.findViewById<TextView>(R.id.tvReportDetails)
            val btnResolve = v.findViewById<Button>(R.id.btnResolve)
            val btnBan = v.findViewById<Button>(R.id.btnBan)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val doc = list[position]
            holder.target.text = "Target: ${doc.getString("targetId")}"
            holder.reason.text = "Reason: ${doc.getString("reason")}"
            holder.details.text = "Details: ${doc.getString("details")}"
            
            holder.btnResolve.setOnClickListener {
                db.collection("reports").document(doc.id).update("status", "resolved")
                Toast.makeText(this@ReportQueueActivity, "Resolved", Toast.LENGTH_SHORT).show()
                loadReports()
            }
            
            holder.btnBan.setOnClickListener {
                Toast.makeText(this@ReportQueueActivity, "User Banned", Toast.LENGTH_SHORT).show()
                db.collection("reports").document(doc.id).update("status", "banned")
                loadReports()
            }
        }

        override fun getItemCount() = list.size
    }
}