package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.RoomAdapter
import com.elu.app.adapter.UserAdapter
import com.elu.app.model.RoomModel
import com.elu.app.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ExploreActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val recyclerViewRooms = findViewById<RecyclerView>(R.id.recyclerViewRooms)
        recyclerViewRooms.layoutManager = LinearLayoutManager(this)

        val recyclerViewPeople = findViewById<RecyclerView>(R.id.recyclerViewPeople)
        recyclerViewPeople.layoutManager = LinearLayoutManager(this)

        loadRooms(recyclerViewRooms)
        loadPeople(recyclerViewPeople)
    }

    private fun loadRooms(recyclerView: RecyclerView) {
        db.collection("rooms")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.documents.map { doc ->
                    RoomModel(
                        roomId = doc.id,
                        title = doc.getString("title") ?: "",
                        hostName = doc.getString("hostName") ?: "",
                        participantCount = doc.getLong("participantCount") ?: 0
                    )
                }
                recyclerView.adapter = RoomAdapter(rooms) { room ->
                    val intent = Intent(this, RoomDetailActivity::class.java)
                    intent.putExtra("roomId", room.roomId)
                    intent.putExtra("roomTitle", room.title)
                    startActivity(intent)
                }
            }
    }

    private fun loadPeople(recyclerView: RecyclerView) {
        val myUid = auth.currentUser?.uid
        db.collection("users").limit(10).get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents
                    .filter { it.id != myUid }
                    .map { doc ->
                        UserModel(
                            uid = doc.id,
                            name = doc.getString("name") ?: "",
                            bio = doc.getString("bio") ?: ""
                        )
                    }
                recyclerView.adapter = UserAdapter(users) { user ->
                    val intent = Intent(this, OtherUserProfileActivity::class.java)
                    intent.putExtra("uid", user.uid)
                    startActivity(intent)
                }
            }
    }
}
