package com.elu.app

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.RoomAdapter
import com.elu.app.model.RoomModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerViewRooms)
        emptyState = findViewById(R.id.emptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        animateMascot()

        // Create room entry points
        findViewById<android.view.View>(R.id.btnQuickCreate).setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
        findViewById<android.view.View>(R.id.cardCreateRoom).setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }
        findViewById<android.view.View>(R.id.btnGo).setOnClickListener {
            startActivity(Intent(this, CreateRoomActivity::class.java))
        }

        // Header tabs
        findViewById<android.view.View>(R.id.tabMine).setOnClickListener { setActiveTab(R.id.tabMine) }
        findViewById<android.view.View>(R.id.tabPopular).setOnClickListener { setActiveTab(R.id.tabPopular) }
        findViewById<android.view.View>(R.id.tabParty).setOnClickListener {
            startActivity(Intent(this, RoomListActivity::class.java))
        }

        // Filter chips (visual state only - all query the same live room list for now)
        findViewById<android.view.View>(R.id.chipRecent).setOnClickListener { setActiveChip(R.id.chipRecent) }
        findViewById<android.view.View>(R.id.chipFollow).setOnClickListener { setActiveChip(R.id.chipFollow) }
        findViewById<android.view.View>(R.id.chipFriends).setOnClickListener { setActiveChip(R.id.chipFriends) }

        // Bottom nav
        findViewById<android.view.View>(R.id.navHome).setOnClickListener { /* already here */ }
        findViewById<android.view.View>(R.id.navGame).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
        findViewById<android.view.View>(R.id.navMoment).setOnClickListener {
            startActivity(Intent(this, MomentActivity::class.java))
        }
        findViewById<android.view.View>(R.id.navMessages).setOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java))
        }
        findViewById<android.view.View>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadRooms()
    }

    private fun loadRooms() {
        db.collection("rooms")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(30)
            .get()
            .addOnSuccessListener { snapshot ->
                val rooms = snapshot.documents.map { doc ->
                    RoomModel(
                        roomId = doc.id,
                        roomShortId = doc.getString("roomShortId") ?: "",
                        title = doc.getString("title") ?: "",
                        emoji = doc.getString("emoji") ?: "🎮",
                        hostName = doc.getString("hostName") ?: "",
                        participantCount = doc.getLong("participantCount") ?: 0
                    )
                }
                if (rooms.isEmpty()) {
                    recyclerView.visibility = android.view.View.GONE
                    emptyState.visibility = android.view.View.VISIBLE
                } else {
                    emptyState.visibility = android.view.View.GONE
                    recyclerView.visibility = android.view.View.VISIBLE
                    recyclerView.adapter = RoomAdapter(rooms) { room ->
                        val intent = Intent(this, RoomDetailActivity::class.java)
                        intent.putExtra("roomId", room.roomId)
                        intent.putExtra("roomTitle", room.title)
                        startActivity(intent)
                    }
                }
            }
    }

    private fun setActiveTab(activeId: Int) {
        val tabs = listOf(R.id.tabMine, R.id.tabPopular)
        for (id in tabs) {
            val tab = findViewById<TextView>(id)
            tab.alpha = if (id == activeId) 1f else 0.55f
        }
    }

    private fun setActiveChip(activeId: Int) {
        val chips = listOf(R.id.chipRecent, R.id.chipFollow, R.id.chipFriends)
        for (id in chips) {
            val chip = findViewById<TextView>(id)
            if (id == activeId) {
                chip.setBackgroundResource(R.drawable.bg_pill_yellow)
                chip.setTextColor(getColor(R.color.text_dark))
            } else {
                chip.setBackgroundResource(R.drawable.bg_pill_white)
                chip.setTextColor(getColor(R.color.text_muted))
            }
        }
    }

    // Simple float + gentle rotation loop on the mascot, mirroring the CSS
    // "bob" animation used in the design preview.
    private fun animateMascot() {
        val mascot = findViewById<ImageView>(R.id.ivMascot)
        val floatUp = ObjectAnimator.ofFloat(mascot, "translationY", 0f, -16f, 0f)
        floatUp.duration = 2400
        floatUp.repeatCount = ValueAnimator.INFINITE
        floatUp.start()

        val rotate = ObjectAnimator.ofFloat(mascot, "rotation", -2f, 2f, -2f)
        rotate.duration = 2400
        rotate.repeatCount = ValueAnimator.INFINITE
        rotate.start()
    }
}
