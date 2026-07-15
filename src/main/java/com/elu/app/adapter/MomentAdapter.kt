package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.R
import com.elu.app.model.MomentModel

class MomentAdapter(private val moments: List<MomentModel>) :
    RecyclerView.Adapter<MomentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val tvText: TextView = view.findViewById(R.id.tvText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_moment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moment = moments[position]
        holder.tvAvatar.text = if (moment.authorName.isNotEmpty()) moment.authorName.take(1).uppercase() else "U"
        holder.tvAuthor.text = moment.authorName.ifEmpty { "Unknown" }
        holder.tvText.text = moment.text
    }

    override fun getItemCount(): Int = moments.size
}
