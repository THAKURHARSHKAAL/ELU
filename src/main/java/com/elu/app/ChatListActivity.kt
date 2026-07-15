package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.ChatAdapter
import com.elu.app.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.tvScreenTitle).text = "Messages"
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        loadChats()
    }

    private fun loadChats() {
        val myUid = auth.currentUser?.uid ?: return
        db.collection("chats")
            .whereArrayContains("participantUids", myUid)
            .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val chatDocs = snapshot.documents
                if (chatDocs.isEmpty()) {
                    tvEmptyState.visibility = android.view.View.VISIBLE
                    return@addOnSuccessListener
                }
                tvEmptyState.visibility = android.view.View.GONE

                val chats = mutableListOf<ChatModel>()
                var loaded = 0
                for (doc in chatDocs) {
                    val participants = doc.get("participantUids") as? List<String> ?: emptyList()
                    val otherUid = participants.firstOrNull { it != myUid } ?: ""
                    db.collection("users").document(otherUid).get()
                        .addOnSuccessListener { userDoc ->
                            chats.add(
                                ChatModel(
                                    chatId = doc.id,
                                    participantUids = participants,
                                    lastMessage = doc.getString("lastMessage") ?: "",
                                    lastMessageTimestamp = doc.getLong("lastMessageTimestamp") ?: 0,
                                    otherUserName = userDoc.getString("name") ?: "Unknown",
                                    otherUserUid = otherUid
                                )
                            )
                            loaded++
                            if (loaded == chatDocs.size) {
                                val sorted = chats.sortedByDescending { it.lastMessageTimestamp }
                                recyclerView.adapter = ChatAdapter(sorted) { chat ->
                                    val intent = Intent(this, ConversationActivity::class.java)
                                    intent.putExtra("otherUid", chat.otherUserUid)
                                    intent.putExtra("otherName", chat.otherUserName)
                                    startActivity(intent)
                                }
                            }
                        }
                }
            }
    }
}
