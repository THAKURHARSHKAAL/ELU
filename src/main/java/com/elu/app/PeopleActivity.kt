package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.UserAdapter
import com.elu.app.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PeopleActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.tvScreenTitle).text = "People"
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        loadUsers()
    }

    private fun loadUsers() {
        val myUid = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("users").get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents
                    .filter { it.id != myUid }
                    .map { doc ->
                        UserModel(
                            uid = doc.id,
                            name = doc.getString("name") ?: "",
                            email = doc.getString("email") ?: "",
                            diamonds = doc.getLong("diamonds") ?: 0,
                            bio = doc.getString("bio") ?: ""
                        )
                    }
                tvEmptyState.visibility = if (users.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                recyclerView.adapter = UserAdapter(users) { user ->
                    val intent = Intent(this, ConversationActivity::class.java)
                    intent.putExtra("otherUid", user.uid)
                    intent.putExtra("otherName", user.name)
                    startActivity(intent)
                }
            }
    }
}
