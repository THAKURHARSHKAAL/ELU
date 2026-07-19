package com.elu.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class UserManagementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        db = FirebaseFirestore.getInstance()
        rv = findViewById(R.id.rvUsers)
        rv.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        loadUsers()
    }

    private fun loadUsers() {
        db.collection("users")
            .limit(100)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents
                rv.adapter = UserManagementAdapter(users)
            }
    }

    inner class UserManagementAdapter(private val list: List<com.google.firebase.firestore.DocumentSnapshot>) : 
        RecyclerView.Adapter<UserManagementAdapter.VH>() {
        
        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val name = v.findViewById<TextView>(R.id.tvMemberName)
            val btnKick = v.findViewById<Button>(R.id.btnKickMember)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val doc = list[position]
            holder.name.text = doc.getString("name") ?: "User"
            holder.btnKick.text = "Ban"
            holder.btnKick.visibility = View.VISIBLE
            holder.btnKick.setOnClickListener {
                Toast.makeText(this@UserManagementActivity, "User Banned", Toast.LENGTH_SHORT).show()
                db.collection("users").document(doc.id).update("banned", true)
            }
        }

        override fun getItemCount() = list.size
    }
}