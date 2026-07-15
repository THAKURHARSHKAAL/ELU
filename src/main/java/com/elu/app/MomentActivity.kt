package com.elu.app

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.MomentAdapter
import com.elu.app.model.MomentModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// A lightweight social feed - short text posts, newest first.
class MomentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.btnPost).setOnClickListener { showPostDialog() }

        loadMoments()
    }

    private fun loadMoments() {
        db.collection("moments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val moments = snapshot.documents.map { doc ->
                    MomentModel(
                        momentId = doc.id,
                        authorUid = doc.getString("authorUid") ?: "",
                        authorName = doc.getString("authorName") ?: "",
                        text = doc.getString("text") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0
                    )
                }
                tvEmptyState.visibility = if (moments.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                recyclerView.adapter = MomentAdapter(moments)
            }
    }

    private fun showPostDialog() {
        val input = EditText(this)
        input.hint = "What's on your mind?"

        AlertDialog.Builder(this)
            .setTitle("New moment")
            .setView(input)
            .setPositiveButton("Post") { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) postMoment(text)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun postMoment(text: String) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get().addOnSuccessListener { userDoc ->
            val name = userDoc.getString("name") ?: "Someone"
            val moment = hashMapOf(
                "authorUid" to uid,
                "authorName" to name,
                "text" to text,
                "createdAt" to System.currentTimeMillis()
            )
            db.collection("moments").add(moment).addOnSuccessListener { loadMoments() }
        }
    }
}
