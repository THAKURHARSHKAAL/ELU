package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.R
import com.elu.app.model.UserModel

class UserAdapter(
    private val users: List<UserModel>,
    private val showDiamondsAsSubtitle: Boolean = false,
    private val onChatClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        val btnAction: TextView = view.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.tvAvatar.text = if (user.name.isNotEmpty()) user.name.take(1).uppercase() else "U"
        holder.tvName.text = user.name.ifEmpty { "Unnamed" }
        holder.tvSubtitle.text = if (showDiamondsAsSubtitle) {
            "${user.points} points"
        } else {
            user.bio.ifEmpty { "Say hi 👋" }
        }
        holder.btnAction.text = if (showDiamondsAsSubtitle) "#${position + 1}" else "Chat"
        holder.btnAction.setOnClickListener { onChatClick(user) }
        holder.itemView.setOnClickListener { onChatClick(user) }
    }

    override fun getItemCount(): Int = users.size
}
