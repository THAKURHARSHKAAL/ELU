package com.elu.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnLudo).setOnClickListener {
            startActivity(Intent(this, LudoActivity::class.java))
        }

        findViewById<View>(R.id.btnDomino).setOnClickListener {
            startActivity(Intent(this, DominoActivity::class.java))
        }

        findViewById<View>(R.id.btnUno).setOnClickListener {
            startActivity(Intent(this, UnoActivity::class.java))
        }

        findViewById<View>(R.id.btnSlots).setOnClickListener {
            startActivity(Intent(this, SlotsActivity::class.java))
        }

        findViewById<View>(R.id.btnLeaderboard).setOnClickListener {
            startActivity(Intent(this, GameLeaderboardActivity::class.java))
        }

        findViewById<View>(R.id.btnGameRules).setOnClickListener {
            showGameRules()
        }
    }

    private fun showGameRules() {
        val rules = "1. Be fair to all players.\n2. No cheating or exploitation.\n3. Respect game hosts.\n4. Enjoy and have fun!"
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Game Rules")
            .setMessage(rules)
            .setPositiveButton("OK", null)
            .show()
    }
}
