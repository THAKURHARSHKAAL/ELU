package com.elu.app

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d("EluSplash", "SplashActivity onCreate")

        val mascot = findViewById<ImageView>(R.id.ivSplashLogo)
        if (mascot == null) {
            Log.e("EluSplash", "ivSplashLogo NOT FOUND")
        }
        val floatUp = ObjectAnimator.ofFloat(mascot, "translationY", 0f, -18f, 0f)
        floatUp.duration = 1200
        floatUp.repeatCount = ValueAnimator.INFINITE
        floatUp.start()

        // Show the mascot briefly, then decide where to send the user
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("EluSplash", "Checking Firebase user")
            val currentUser = FirebaseAuth.getInstance().currentUser
            Log.d("EluSplash", "User: $currentUser")
            val nextActivity = if (currentUser != null) {
                HomeActivity::class.java
            } else {
                LoginActivity::class.java
            }
            Log.d("EluSplash", "Starting ${nextActivity.simpleName}")
            startActivity(Intent(this, nextActivity))
            finish()
        }, 1500)
    }
}
