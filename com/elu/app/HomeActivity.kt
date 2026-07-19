package com.elu.app

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.CategoryAdapter
import com.elu.app.adapter.RoomAdapter
import com.elu.app.model.CategoryModel
import com.elu.app.model.RoomModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var rvCategories: RecyclerView
    private lateinit var rvRegions: RecyclerView
    private lateinit var emptyState: LinearLayout
    private var selectedCategory: String? = null
    private var selectedRegion: String? = null

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
        rvCategories = findViewById(R.id.rvCategories)
        rvRegions = findViewById(R.id.rvRegions)
        
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        setupCategories()
        setupRegions()

        animateMascot()

        // Search
        findViewById<android.view.View>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Notifications
        findViewById<android.view.View>(R.id.btnNotifications).setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        // Rankings
        findViewById<android.view.View>(R.id.btnRankings).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        // Event Banner
        findViewById<android.view.View>(R.id.cardEvent).setOnClickListener {
            startActivity(Intent(this, EventLandingActivity::class.java))
        }

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

    private fun setupCategories() {
        val categories = listOf(
            CategoryModel("all", "All", "🏠"),
            CategoryModel("music", "Music", "🎵"),
            CategoryModel("games", "Games", "🎮"),
            CategoryModel("chat", "Chat", "💬"),
            CategoryModel("karaoke", "Karaoke", "🎤"),
            CategoryModel("dating", "Dating", "❤️"),
            CategoryModel("pk", "PK Battle", "⚔️")
        )
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCategories.adapter = CategoryAdapter(categories) { category ->
            selectedCategory = if (category.id == "all") null else category.id
            loadRooms()
        }
    }

    private fun setupRegions() {
        val regions = listOf(
            CategoryModel("global", "Global", "🌍"),
            CategoryModel("me", "Middle East", "🕌"),
            CategoryModel("asia", "Asia", "🌏"),
            CategoryModel("eu", "Europe", "🏰"),
            CategoryModel("na", "N. America", "🗽")
        )
        rvRegions.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvRegions.adapter = CategoryAdapter(regions) { region ->
            selectedRegion = if (region.id == "global") null else region.id
            loadRooms()
        }
    }

    private fun loadRooms() {
        var query: Query = db.collection("rooms")
            .orderBy("createdAt", Query.Direction.DESCENDING)

        if (selectedCategory != null) {
            query = query.whereEqualTo("category", selectedCategory)
        }
        if (selectedRegion != null) {
            query = query.whereEqualTo("region", selectedRegion)
        }

        query.limit(30)
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
                    recyclerView.adapter = RoomAdapter(rooms, isGrid = true) { room ->
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
