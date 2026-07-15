package com.elu.app

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.MessageAdapter
import com.elu.app.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// 1:1 direct message conversation between the current user and "otherUid".
class ConversationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var chatId: String
    private lateinit var otherUid: String
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        otherUid = intent.getStringExtra("otherUid") ?: run { finish(); return }
        val otherName = intent.getStringExtra("otherName") ?: "Chat"
        val myUid = auth.currentUser?.uid ?: run { finish(); return }

        // Deterministic chat id so both users land in the same document
        chatId = listOf(myUid, otherUid).sorted().joinToString("_")

        findViewById<TextView>(R.id.tvConversationTitle).text = otherName
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val etMessage = findViewById<EditText>(R.id.etMessage)
        findViewById<TextView>(R.id.btnSend).setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text, myUid)
                etMessage.setText("")
            }
        }

        ensureChatDocument(myUid)
        listenForMessages(myUid)
    }

    private fun ensureChatDocument(myUid: String) {
        val chatRef = db.collection("chats").document(chatId)
        chatRef.get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                chatRef.set(
                    hashMapOf(
                        "participantUids" to listOf(myUid, otherUid),
                        "lastMessage" to "",
                        "lastMessageTimestamp" to System.currentTimeMillis()
                    )
                )
            }
        }
    }

    private fun sendMessage(text: String, myUid: String) {
        db.collection("users").document(myUid).get().addOnSuccessListener { userDoc ->
            val name = userDoc.getString("name") ?: "Someone"
            val message = hashMapOf(
                "senderUid" to myUid,
                "senderName" to name,
                "text" to text,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("chats").document(chatId).collection("messages").add(message)
            db.collection("chats").document(chatId).update(
                mapOf(
                    "lastMessage" to text,
                    "lastMessageTimestamp" to System.currentTimeMillis()
                )
            )
        }
    }

    private fun listenForMessages(myUid: String) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot == null) return@addSnapshotListener
                val messages = snapshot.documents.map { doc ->
                    MessageModel(
                        messageId = doc.id,
                        senderUid = doc.getString("senderUid") ?: "",
                        senderName = doc.getString("senderName") ?: "",
                        text = doc.getString("text") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0
                    )
                }
                recyclerView.adapter = MessageAdapter(messages, myUid)
                if (messages.isNotEmpty()) recyclerView.scrollToPosition(messages.size - 1)
            }
    }
}
