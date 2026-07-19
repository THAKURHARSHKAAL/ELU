package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.UserAdapter
import com.elu.app.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class SocialListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_list)

        val title = intent.getStringExtra("title") ?: "Social List"
        findViewById<TextView>(R.id.tvTitle).text = title
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvSocial)
        rv.layoutManager = LinearLayoutManager(this)

        loadUsers(rv)
    }

    private fun loadUsers(rv: RecyclerView) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").limit(20).get().addOnSuccessListener { snapshot ->
            val users = snapshot.documents.map { doc ->
                UserModel(
                    uid = doc.id,
                    name = doc.getString("name") ?: "User",
                    bio = doc.getString("bio") ?: ""
                )
            }
            rv.adapter = UserAdapter(users) { user ->
                if (title == "Select Contact") {
                    val intent = Intent(this, ConversationActivity::class.java)
                    intent.putExtra("otherUid", user.uid)
                    intent.putExtra("otherName", user.name)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, OtherUserProfileActivity::class.java)
                    intent.putExtra("uid", user.uid)
                    startActivity(intent)
                }
            }
        }
    }
}