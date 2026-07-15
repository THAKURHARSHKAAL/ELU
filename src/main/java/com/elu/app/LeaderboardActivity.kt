package com.elu.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.UserAdapter
import com.elu.app.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.tvScreenTitle).text = "Leaderboard"
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        db.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    UserModel(
                        uid = doc.id,
                        name = doc.getString("name") ?: "",
                        points = doc.getLong("points") ?: 0
                    )
                }
                tvEmptyState.visibility = if (users.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                // No click action needed here - pass a no-op
                recyclerView.adapter = UserAdapter(users, showDiamondsAsSubtitle = true) { }
            }
    }
}
