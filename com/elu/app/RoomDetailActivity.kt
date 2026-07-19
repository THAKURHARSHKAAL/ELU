package com.elu.app

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.MemberAdapter
import com.elu.app.adapter.MessageAdapter
import com.elu.app.model.GiftModel
import com.elu.app.model.MessageModel
import com.elu.app.model.ParticipantModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class RoomDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var roomId: String
    private lateinit var recyclerView: RecyclerView
    private var micOn = true
    private var isHost = false
    private var roomTopic = ""
    private var isRoomLocked = false

    private val seatCount = 8
    private val userNames = mutableMapOf<String, String>()
    
    private var roomListener: ListenerRegistration? = null
    private var messageListener: ListenerRegistration? = null
    private var participantListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_detail)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        roomId = intent.getStringExtra("roomId") ?: run { finish(); return }
        val roomTitle = intent.getStringExtra("roomTitle") ?: "Party room"
        findViewById<TextView>(R.id.tvRoomTitle).text = roomTitle

        findViewById<TextView>(R.id.btnBack).setOnClickListener { 
            showLeaveConfirmation()
        }

        findViewById<View>(R.id.btnPK).setOnClickListener {
            PKBattleBottomSheet().show(supportFragmentManager, "pk")
        }

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            if (isHost) {
                RoomSettingsBottomSheet(roomTopic, isRoomLocked) { newTopic, locked ->
                    updateRoomSettings(newTopic, locked)
                }.show(supportFragmentManager, "settings")
            } else {
                Toast.makeText(this, "Only host can change settings", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<View>(R.id.btnReportRoom).setOnClickListener {
            val intent = android.content.Intent(this, ReportActivity::class.java)
            intent.putExtra("targetId", roomId)
            intent.putExtra("targetType", "room")
            startActivity(intent)
        }

        findViewById<View>(R.id.btnGift).setOnClickListener {
            GiftBottomSheet { gift ->
                sendGift(gift)
            }.show(supportFragmentManager, "gift")
        }

        findViewById<TextView>(R.id.btnAdmin).setOnClickListener { showMemberList() }
        
        showRoomRules()

        val btnMic = findViewById<TextView>(R.id.btnMic)
        btnMic.setOnClickListener {
            micOn = !micOn
            btnMic.text = if (micOn) "🎤" else "🔇"
        }

        recyclerView = findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val etMessage = findViewById<EditText>(R.id.etMessage)
        findViewById<TextView>(R.id.btnSend).setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                sendMessage(text)
                etMessage.setText("")
            }
        }

        checkIfHostAndJoin()
    }

    private fun showLeaveConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Leave Room")
            .setMessage("Are you sure you want to leave this party?")
            .setPositiveButton("Leave") { _, _ ->
                leaveRoom()
                finish()
            }
            .setNegativeButton("Stay", null)
            .show()
    }

    private fun checkIfHostAndJoin() {
        val myUid = auth.currentUser?.uid ?: return
        db.collection("rooms").document(roomId).get().addOnSuccessListener { doc ->
            if (!doc.exists()) {
                Toast.makeText(this, "Room no longer exists", Toast.LENGTH_SHORT).show()
                finish()
                return@addOnSuccessListener
            }
            
            val hostUid = doc.getString("hostUid")
            isHost = hostUid == myUid
            if (isHost) {
                findViewById<View>(R.id.btnAdmin)?.visibility = View.VISIBLE
            }
            
            roomTopic = doc.getString("title") ?: ""
            isRoomLocked = doc.getBoolean("locked") ?: false
            
            val emoji = doc.getString("emoji") ?: "🎮"
            val shortId = doc.getString("roomShortId") ?: roomId.take(6).uppercase()
            findViewById<TextView>(R.id.tvRoomEmoji)?.text = emoji
            findViewById<TextView>(R.id.tvRoomId)?.text = "ID: $shortId"
            
            joinRoom {
                startListeners()
            }
        }.addOnFailureListener { e ->
            if (e.message?.contains("PERMISSION_DENIED") == true) {
                showFirebaseErrorDialog()
            }
        }
    }

    private fun showFirebaseErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Firebase Setup Needed")
            .setMessage("The Firestore API is not enabled. Please enable it in the Google Cloud Console for project elu-07.\n\nAlso, make sure you have published the Security Rules.")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun joinRoom(onComplete: () -> Unit) {
        val myUid = auth.currentUser?.uid ?: return
        db.collection("users").document(myUid).get().addOnSuccessListener { userDoc ->
            val name = userDoc.getString("name") ?: "User"
            userNames[myUid] = name
            
            val participant = hashMapOf(
                "uid" to myUid,
                "name" to name,
                "isHost" to isHost
            )
            db.collection("rooms").document(roomId)
                .collection("participants").document(myUid).set(participant)
                .addOnSuccessListener {
                    db.collection("rooms").document(roomId).update("participantCount", FieldValue.increment(1))
                    onComplete()
                }
        }
    }

    private fun leaveRoom() {
        val myUid = auth.currentUser?.uid ?: return
        db.collection("rooms").document(roomId).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                val seats = doc.get("seats") as? Map<String, String> ?: emptyMap()
                val mySeat = seats.entries.find { it.value == myUid }?.key
                if (mySeat != null) {
                    db.collection("rooms").document(roomId).update("seats.$mySeat", FieldValue.delete())
                }
                
                db.collection("rooms").document(roomId).collection("participants").document(myUid).delete()
                    .addOnSuccessListener {
                        db.collection("rooms").document(roomId).update("participantCount", FieldValue.increment(-1))
                    }
            }
        }
    }

    private fun startListeners() {
        val myUid = auth.currentUser?.uid ?: ""
        
        // Listen for room & seats
        roomListener = db.collection("rooms").document(roomId).addSnapshotListener { snapshot, _ ->
            if (snapshot == null || !snapshot.exists()) return@addSnapshotListener
            val seats = snapshot.get("seats") as? Map<String, String> ?: emptyMap()
            roomTopic = snapshot.getString("title") ?: ""
            isRoomLocked = snapshot.getBoolean("locked") ?: false
            findViewById<TextView>(R.id.tvRoomTitle)?.text = roomTopic
            
            // Sync names and UI
            db.collection("rooms").document(roomId).collection("participants").get()
                .addOnSuccessListener { participantsSnapshot ->
                    val names = mutableMapOf<String, String>()
                    participantsSnapshot.documents.forEach { doc ->
                        names[doc.id] = doc.getString("name") ?: "User"
                    }
                    updateSeatsUI(seats, names)
                }
        }

        // Listen if I am kicked
        participantListener = db.collection("rooms").document(roomId).collection("participants").document(myUid).addSnapshotListener { partDoc, _ ->
            if (partDoc != null && !partDoc.exists()) {
                Toast.makeText(this, "You have been removed from the room", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Listen for messages
        messageListener = db.collection("rooms").document(roomId).collection("messages")
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

    private fun updateSeatsUI(seats: Map<String, String>, names: Map<String, String>) {
        val grid = findViewById<GridLayout>(R.id.seatGrid)
        grid.removeAllViews()
        val inflater = LayoutInflater.from(this)
        val myUid = auth.currentUser?.uid

        for (i in 0 until seatCount) {
            val seatView = inflater.inflate(R.layout.item_seat, grid, false)
            val avatar = seatView.findViewById<TextView>(R.id.tvSeatAvatar)
            val name = seatView.findViewById<TextView>(R.id.tvSeatName)
            val mic = seatView.findViewById<View>(R.id.tvSeatMic)
            val occupantUid = seats[i.toString()]

            if (occupantUid != null) {
                val occupantName = names[occupantUid] ?: "User"
                avatar.text = occupantName.take(1).uppercase()
                name.text = if (occupantUid == myUid) "You" else occupantName
                mic.visibility = View.VISIBLE
                
                seatView.setOnClickListener {
                    if (occupantUid == myUid) {
                        db.collection("rooms").document(roomId).update("seats.$i", FieldValue.delete())
                    } else if (isHost) {
                        AlertDialog.Builder(this)
                            .setTitle("Seat Management")
                            .setMessage("Remove $occupantName from seat?")
                            .setPositiveButton("Remove") { _, _ ->
                                db.collection("rooms").document(roomId).update("seats.$i", FieldValue.delete())
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            } else {
                avatar.text = "+"
                name.text = "Empty"
                mic.visibility = View.GONE
                seatView.setOnClickListener { takeSeat(i) }
            }
            grid.addView(seatView)
        }
    }

    private fun takeSeat(index: Int) {
        val myUid = auth.currentUser?.uid ?: return
        db.collection("rooms").document(roomId).get().addOnSuccessListener { doc ->
            val seats = doc.get("seats") as? Map<String, String> ?: emptyMap()
            val existingSeat = seats.entries.find { it.value == myUid }?.key
            if (existingSeat != null) {
                val updates = hashMapOf<String, Any>(
                    "seats.$existingSeat" to FieldValue.delete(),
                    "seats.$index" to myUid
                )
                db.collection("rooms").document(roomId).update(updates)
            } else {
                db.collection("rooms").document(roomId).update("seats.$index", myUid)
            }
        }
    }

    private fun updateRoomSettings(topic: String, locked: Boolean) {
        val updates = hashMapOf<String, Any>(
            "title" to topic,
            "locked" to locked
        )
        db.collection("rooms").document(roomId).update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendGift(gift: GiftModel) {
        val myUid = auth.currentUser?.uid ?: return
        val name = userNames[myUid] ?: "User"
        
        val giftMsg = hashMapOf(
            "senderUid" to myUid,
            "senderName" to name,
            "text" to "sent a ${gift.name} ${gift.icon}",
            "isGift" to true,
            "giftIcon" to gift.icon,
            "timestamp" to System.currentTimeMillis()
        )
        
        db.collection("rooms").document(roomId).collection("messages").add(giftMsg)
            .addOnSuccessListener {
                showGiftAnimation(gift.icon)
            }
    }

    private fun showGiftAnimation(icon: String) {
        val container = findViewById<FrameLayout>(R.id.giftAnimationContainer)
        val tvAnim = findViewById<TextView>(R.id.tvGiftAnim)
        
        tvAnim.text = icon
        container.visibility = View.VISIBLE
        
        val animator = ObjectAnimator.ofFloat(tvAnim, "translationY", 500f, -500f)
        animator.duration = 2000
        animator.interpolator = AccelerateDecelerateInterpolator()
        
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                container.visibility = View.GONE
            }
        })
        
        animator.start()
        
        // Also a scale animation
        tvAnim.scaleX = 0f
        tvAnim.scaleY = 0f
        tvAnim.animate().scaleX(2f).scaleY(2f).setDuration(1000).start()
    }

    private fun showRoomRules() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_room_rules, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        dialogView.findViewById<Button>(R.id.btnAgree).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showMemberList() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_member_list, null)
        val rvMembers = dialogView.findViewById<RecyclerView>(R.id.rvMembers)
        rvMembers.layoutManager = LinearLayoutManager(this)

        db.collection("rooms").document(roomId).collection("participants").get()
            .addOnSuccessListener { snapshot ->
                val members = snapshot.documents.map { doc ->
                    ParticipantModel(
                        uid = doc.id,
                        name = doc.getString("name") ?: "User",
                        isHost = doc.getBoolean("isHost") ?: false
                    )
                }
                rvMembers.adapter = MemberAdapter(members, isHost) { member ->
                    removeMember(member.uid)
                }
            }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showAdminDialog() {
        db.collection("rooms").document(roomId).collection("participants").get()
            .addOnSuccessListener { snapshot ->
                val participants = snapshot.documents.filter { it.id != auth.currentUser?.uid }
                val names = participants.map { it.getString("name") ?: "User" }.toTypedArray()
                
                if (names.isEmpty()) {
                    Toast.makeText(this, "No other participants to manage", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                AlertDialog.Builder(this)
                    .setTitle("Manage Members")
                    .setItems(names) { _, which ->
                        val targetUid = participants[which].id
                        removeMember(targetUid)
                    }
                    .show()
            }
    }

    private fun removeMember(uid: String) {
        db.collection("rooms").document(roomId).collection("participants").document(uid).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Member removed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessage(text: String) {
        val uid = auth.currentUser?.uid ?: return
        val name = userNames[uid] ?: "User"
        val message = hashMapOf(
            "senderUid" to uid,
            "senderName" to name,
            "text" to text,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("rooms").document(roomId).collection("messages").add(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        roomListener?.remove()
        messageListener?.remove()
        participantListener?.remove()
        leaveRoom()
    }
}
