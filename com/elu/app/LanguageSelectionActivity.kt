package com.elu.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LanguageSelectionActivity : AppCompatActivity() {

    private var selectedIndex = 0
    private val languages = listOf("English", "Arabic", "Hindi", "Urdu", "Indonesian", "Turkish")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        val rvLanguages = findViewById<RecyclerView>(R.id.rvLanguages)
        rvLanguages.layoutManager = LinearLayoutManager(this)
        rvLanguages.adapter = LanguageAdapter(languages)

        findViewById<View>(R.id.btnContinue).setOnClickListener {
            val prefs = getSharedPreferences("elu_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("lang_selected", true).apply()
            prefs.edit().putString("app_lang", languages[selectedIndex]).apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    inner class LanguageAdapter(private val items: List<String>) :
        RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvName.text = items[position]
            holder.rbSelected.isChecked = position == selectedIndex
            holder.itemView.setOnClickListener {
                val oldIndex = selectedIndex
                selectedIndex = position
                notifyItemChanged(oldIndex)
                notifyItemChanged(selectedIndex)
            }
        }

        override fun getItemCount() = items.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvLanguageName)
            val rbSelected: RadioButton = view.findViewById(R.id.rbSelected)
        }
    }
}