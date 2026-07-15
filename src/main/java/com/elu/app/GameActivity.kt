package com.elu.app

import android.os.Bundle
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// A hub of lightweight in-room games (Ludo, Cards, Truth or Dare, Sing-off).
// These are UI entry points only - each game would be its own mini activity
// or, for multiplayer games, a Firestore-backed game-state document per room.
class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val grid = findViewById<GridLayout>(R.id.gameGrid)
        for (i in 0 until grid.childCount) {
            grid.getChildAt(i).setOnClickListener {
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
