package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.RoomAdapter
import com.elu.app.model.RoomModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RoomListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_list)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.btnCreateRoom).setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadRooms() // refresh in case a room was just created
    }

    private fun loadRooms() {
        db.collection("rooms")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.documents.map { doc ->
                    RoomModel(
                        roomId = doc.id,
                        roomShortId = doc.getString("roomShortId") ?: "",
                        title = doc.getString("title") ?: "",
                        emoji = doc.getString("emoji") ?: "🎮",
                        hostUid = doc.getString("hostUid") ?: "",
                        hostName = doc.getString("hostName") ?: "",
                        participantCount = doc.getLong("participantCount") ?: 0,
                        createdAt = doc.getLong("createdAt") ?: 0
                    )
                }
                tvEmptyState.visibility = if (rooms.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
                recyclerView.adapter = RoomAdapter(rooms) { room ->
                    val intent = Intent(this, RoomDetailActivity::class.java)
                    intent.putExtra("roomId", room.roomId)
                    intent.putExtra("roomTitle", room.title)
                    startActivity(intent)
                }
            }
    }
}
