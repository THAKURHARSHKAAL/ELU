package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.R
import com.elu.app.model.ChatModel

class ChatAdapter(
    private val chats: List<ChatModel>,
    private val onChatClick: (ChatModel) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvChatName: TextView = view.findViewById(R.id.tvChatName)
        val tvLastMessage: TextView = view.findViewById(R.id.tvLastMessage)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        if (chat.isGroup) {
            holder.tvAvatar.text = if (chat.groupName.isNotEmpty()) chat.groupName.take(1).uppercase() else "G"
            holder.tvChatName.text = chat.groupName.ifEmpty { "Group Chat" }
        } else {
            holder.tvAvatar.text = if (chat.otherUserName.isNotEmpty()) chat.otherUserName.take(1).uppercase() else "U"
            holder.tvChatName.text = chat.otherUserName.ifEmpty { "Unknown" }
        }

        holder.tvLastMessage.text = chat.lastMessage.ifEmpty { "Say hi 👋" }
        
        if (chat.lastMessageTimestamp > 0) {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            holder.tvTime.text = sdf.format(java.util.Date(chat.lastMessageTimestamp))
        } else {
            holder.tvTime.text = ""
        }

        holder.itemView.setOnClickListener { onChatClick(chat) }
    }

    override fun getItemCount(): Int = chats.size
}
