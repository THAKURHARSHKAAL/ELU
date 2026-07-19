package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WalletActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnRecharge).setOnClickListener {
            startActivity(Intent(this, RechargeActivity::class.java))
        }

        findViewById<View>(R.id.btnVip).setOnClickListener {
            startActivity(Intent(this, VipCenterActivity::class.java))
        }

        findViewById<View>(R.id.btnWithdraw).setOnClickListener {
            startActivity(Intent(this, WithdrawActivity::class.java))
        }

        findViewById<View>(R.id.btnHistory).setOnClickListener {
            Toast.makeText(this, "Transaction history coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnHostApply).setOnClickListener {
            Toast.makeText(this, "Host Application system coming soon", Toast.LENGTH_SHORT).show()
        }

        loadBalance()
    }

    private fun loadBalance() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val diamonds = snapshot.getLong("diamonds") ?: 0
                findViewById<TextView>(R.id.tvWalletDiamonds).text = diamonds.toString()
            }
        }
    }
}