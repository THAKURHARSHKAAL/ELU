package com.elu.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.NotificationAdapter
import com.elu.app.model.NotificationModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var rv: RecyclerView
    private lateinit var tvEmpty: TextView
    private var currentType = "activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        rv = findViewById(R.id.rvNotifications)
        tvEmpty = findViewById(R.id.tvEmpty)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentType = if (tab?.position == 0) "activity" else "system"
                loadNotifications()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        loadNotifications()
    }

    private fun loadNotifications() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("notifications")
            .whereEqualTo("type", currentType)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.map { doc ->
                    NotificationModel(
                        id = doc.id,
                        type = doc.getString("type") ?: "activity",
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0,
                        icon = doc.getString("icon") ?: "🔔"
                    )
                }
                
                if (list.isEmpty()) {
                    tvEmpty.visibility = View.VISIBLE
                    rv.adapter = null
                } else {
                    tvEmpty.visibility = View.GONE
                    rv.adapter = NotificationAdapter(list)
                }
            }
            .addOnFailureListener {
                // If it fails, maybe due to missing index, show empty for now
                tvEmpty.visibility = View.VISIBLE
            }
    }
}