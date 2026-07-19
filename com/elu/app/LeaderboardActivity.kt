package com.elu.app

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.RoomAdapter
import com.elu.app.adapter.UserAdapter
import com.elu.app.model.RoomModel
import com.elu.app.model.UserModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private var rankingType = 0 // 0: User, 1: Room, 2: Host, 3: Family
    private var timeType = "daily"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        db = FirebaseFirestore.getInstance()
        recyclerView = findViewById(R.id.recyclerView)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.tvScreenTitle).text = "Rankings"
        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        setupTabs()
        loadLeaderboard()
    }

    private fun setupTabs() {
        val tabRankingType = findViewById<TabLayout>(R.id.tabRankingType)
        tabRankingType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                rankingType = tab?.position ?: 0
                loadLeaderboard()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        findViewById<View>(R.id.tabDaily).setOnClickListener { setTimeType("daily") }
        findViewById<View>(R.id.tabWeekly).setOnClickListener { setTimeType("weekly") }
        findViewById<View>(R.id.tabMonthly).setOnClickListener { setTimeType("monthly") }
    }

    private fun setTimeType(type: String) {
        timeType = type
        val tabs = mapOf("daily" to R.id.tabDaily, "weekly" to R.id.tabWeekly, "monthly" to R.id.tabMonthly)
        tabs.forEach { (key, id) ->
            val view = findViewById<TextView>(id)
            if (key == type) {
                view.setBackgroundResource(R.drawable.bg_pill_yellow)
                view.setTextColor(getColor(R.color.text_dark))
            } else {
                view.setBackgroundResource(R.drawable.bg_pill_white)
                view.setTextColor(getColor(R.color.text_muted))
            }
        }
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        when (rankingType) {
            0 -> loadUserRanking()
            1 -> loadRoomRanking()
            2 -> loadHostRanking()
            3 -> loadFamilyRanking()
        }
    }

    private fun loadUserRanking() {
        db.collection("users")
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.map { doc ->
                    UserModel(
                        uid = doc.id,
                        name = doc.getString("name") ?: "",
                        points = doc.getLong("points") ?: 0
                    )
                }
                updateUserList(users)
            }
    }

    private fun loadRoomRanking() {
        db.collection("rooms")
            .orderBy("participantCount", Query.Direction.DESCENDING)
            .limit(50)
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
                updateRoomList(rooms)
            }
    }

    private fun loadHostRanking() {
        // For now, same as user ranking or based on a 'hostScore' field
        loadUserRanking() 
    }

    private fun loadFamilyRanking() {
        // Placeholder for family ranking
        tvEmptyState.visibility = View.VISIBLE
        recyclerView.adapter = null
    }

    private fun updateUserList(users: List<UserModel>) {
        tvEmptyState.visibility = if (users.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.adapter = UserAdapter(users, showDiamondsAsSubtitle = true) { user ->
            val intent = android.content.Intent(this, OtherUserProfileActivity::class.java)
            intent.putExtra("uid", user.uid)
            startActivity(intent)
        }
    }

    private fun updateRoomList(rooms: List<RoomModel>) {
        tvEmptyState.visibility = if (rooms.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.adapter = RoomAdapter(rooms) { room ->
            val intent = android.content.Intent(this, RoomDetailActivity::class.java)
            intent.putExtra("roomId", room.roomId)
            intent.putExtra("roomTitle", room.title)
            startActivity(intent)
        }
    }
}
