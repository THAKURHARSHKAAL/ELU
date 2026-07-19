package com.elu.app

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.MessageAdapter
import com.elu.app.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class GroupChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var groupId: String
    private lateinit var recyclerView: RecyclerView
    private var messageListener: ListenerRegistration? = null
    private var myName = "User"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        groupId = intent.getStringExtra("groupId") ?: run { finish(); return }
        val groupName = intent.getStringExtra("groupName") ?: "Group Chat"
        val myUid = auth.currentUser?.uid ?: run { finish(); return }

        findViewById<TextView>(R.id.tvGroupTitle).text = groupName
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }

        val etMessage = findViewById<EditText>(R.id.etMessage)
        findViewById<View>(R.id.btnSend).setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text, myUid)
                etMessage.setText("")
            }
        }

        fetchMyName(myUid)
        listenForMessages(myUid)
    }

    private fun fetchMyName(myUid: String) {
        db.collection("users").document(myUid).get().addOnSuccessListener { 
            myName = it.getString("name") ?: "User"
        }
    }

    private fun sendMessage(text: String, myUid: String) {
        val message = hashMapOf(
            "senderUid" to myUid,
            "senderName" to myName,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("groups").document(groupId).collection("messages").add(message)
        db.collection("groups").document(groupId).update(
            mapOf(
                "lastMessage" to text,
                "lastMessageTimestamp" to System.currentTimeMillis()
            )
        )
    }

    private fun listenForMessages(myUid: String) {
        messageListener = db.collection("groups").document(groupId).collection("messages")
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

    override fun onDestroy() {
        super.onDestroy()
        messageListener?.remove()
    }
}