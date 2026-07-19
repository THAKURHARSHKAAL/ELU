package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InterestSelectionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val interests = listOf(
        "Gaming", "Music", "Movies", "Tech", "Art", 
        "Cooking", "Sports", "Travel", "Fashion", "Anime",
        "Dating", "Chatting", "Books", "Fitness", "Education"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interest_selection)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupInterests)
        for (interest in interests) {
            val chip = Chip(this)
            chip.text = interest
            chip.isCheckable = true
            chipGroup.addView(chip)
        }

        findViewById<Button>(R.id.btnNext).setOnClickListener {
            val selectedInterests = mutableListOf<String>()
            for (i in 0 until chipGroup.childCount) {
                val chip = chipGroup.getChildAt(i) as Chip
                if (chip.isChecked) {
                    selectedInterests.add(chip.text.toString())
                }
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            db.collection("users").document(uid).update("interests", selectedInterests)
                .addOnCompleteListener {
                    startActivity(Intent(this, PermissionsActivity::class.java))
                    finish()
                }
        }
    }
}