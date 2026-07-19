package com.elu.app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var selectedGender = "Male"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etNickname = findViewById<EditText>(R.id.etNickname)
        val etBirthday = findViewById<EditText>(R.id.etBirthday)
        val btnMale = findViewById<Button>(R.id.btnMale)
        val btnFemale = findViewById<Button>(R.id.btnFemale)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnMale.setOnClickListener {
            selectedGender = "Male"
            btnMale.setBackgroundTintList(getColorStateList(R.color.elu_blue))
            btnFemale.setBackgroundTintList(getColorStateList(R.color.card_white))
        }

        btnFemale.setOnClickListener {
            selectedGender = "Female"
            btnFemale.setBackgroundTintList(getColorStateList(R.color.elu_pink))
            btnMale.setBackgroundTintList(getColorStateList(R.color.card_white))
        }

        etBirthday.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                etBirthday.setText("$year-${month + 1}-$day")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSave.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            val birthday = etBirthday.text.toString().trim()

            if (nickname.isEmpty() || birthday.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uid = auth.currentUser?.uid ?: return@setOnClickListener
            val updates = hashMapOf(
                "nickname" to nickname,
                "gender" to selectedGender,
                "birthday" to birthday,
                "setupComplete" to true
            )

            db.collection("users").document(uid).update(updates as Map<String, Any>)
                .addOnSuccessListener {
                    startActivity(Intent(this, InterestSelectionActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    // If document doesn't exist, create it
                    db.collection("users").document(uid).set(updates)
                        .addOnSuccessListener {
                            startActivity(Intent(this, InterestSelectionActivity::class.java))
                            finish()
                        }
                }
        }
    }
}