package com.elu.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.RoomAdapter
import com.elu.app.adapter.UserAdapter
import com.elu.app.model.RoomModel
import com.elu.app.model.UserModel
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rvResults: RecyclerView
    private var currentTab = 0 // 0: Rooms, 1: Users, 2: Hashtags

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        db = FirebaseFirestore.getInstance()
        rvResults = findViewById(R.id.rvSearchResults)
        rvResults.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                performSearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) {
            rvResults.adapter = null
            return
        }

        when (currentTab) {
            0 -> searchRooms(query)
            1 -> searchUsers(query)
            2 -> searchHashtags(query)
        }
    }

    private fun searchRooms(query: String) {
        db.collection("rooms")
            .whereGreaterThanOrEqualTo("title", query)
            .whereLessThanOrEqualTo("title", query + "\uf8ff")
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.documents.map { doc ->
                    RoomModel(
                        roomId = doc.id,
                        title = doc.getString("title") ?: "",
                        emoji = doc.getString("emoji") ?: "🎮",
                        hostName = doc.getString("hostName") ?: "",
                        participantCount = doc.getLong("participantCount") ?: 0
                    )
                }
                rvResults.adapter = RoomAdapter(rooms) { /* handle click */ }
            }
    }

    private fun searchUsers(query: String) {
        db.collection("users")
            .whereGreaterThanOrEqualTo("nickname", query)
            .whereLessThanOrEqualTo("nickname", query + "\uf8ff")
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    UserModel(
                        uid = doc.id,
                        name = doc.getString("nickname") ?: doc.getString("name") ?: "",
                        bio = doc.getString("bio") ?: ""
                    )
                }
                rvResults.adapter = UserAdapter(users) { user ->
                    val intent = android.content.Intent(this, OtherUserProfileActivity::class.java)
                    intent.putExtra("uid", user.uid)
                    startActivity(intent)
                }
            }
    }

    private fun searchHashtags(query: String) {
        // Simple hashtag search placeholder
    }
}