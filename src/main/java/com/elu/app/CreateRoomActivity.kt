package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedEmoji = "🎮"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val tvEmoji = findViewById<TextView>(R.id.tvSelectedEmoji)
        findViewById<View>(R.id.btnChangeEmoji).setOnClickListener {
            MediaBottomSheet { emoji ->
                selectedEmoji = emoji
                tvEmoji.text = emoji
            }.show(supportFragmentManager, "emoji")
        }

        val etRoomTitle = findViewById<EditText>(R.id.etRoomTitle)
        val btnCreate = findViewById<Button>(R.id.btnCreate)

        btnCreate.setOnClickListener {
            val title = etRoomTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Give your room a name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            createRoom(title, btnCreate)
        }
    }

    private fun createRoom(title: String, btnCreate: Button) {
        val uid = auth.currentUser?.uid ?: return
        btnCreate.isEnabled = false

        // Generate a clean 6-digit room ID
        val roomShortId = (100000 + Random.nextInt(900000)).toString()

        db.collection("users").document(uid).get()
            .addOnCompleteListener { task ->
                val hostName = if (task.isSuccessful) {
                    task.result?.getString("name") ?: "Host"
                } else {
                    "Host"
                }

                val roomData = hashMapOf(
                    "title" to title,
                    "roomShortId" to roomShortId,
                    "emoji" to selectedEmoji,
                    "hostUid" to uid,
                    "hostName" to hostName,
                    "participantCount" to 0L, // Will be incremented when joining RoomDetail
                    "createdAt" to System.currentTimeMillis(),
                    "seats" to hashMapOf("0" to uid) // Auto-seat host at Seat 0
                )

                db.collection("rooms").add(roomData)
                    .addOnSuccessListener { docRef ->
                        val intent = Intent(this, RoomDetailActivity::class.java)
                        intent.putExtra("roomId", docRef.id)
                        intent.putExtra("roomTitle", title)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnCreate.isEnabled = true
                        if (e.message?.contains("PERMISSION_DENIED") == true) {
                            showFirebaseErrorDialog()
                        } else {
                            Toast.makeText(this, "Creation failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
    }

    private fun showFirebaseErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Firebase Permission Denied")
            .setMessage("Could not create room. Please ensure:\n1. Cloud Firestore API is enabled in Google Cloud Console.\n2. Firestore Security Rules are published.\n3. Your internet is working.")
            .setPositiveButton("OK", null)
            .show()
    }
}
