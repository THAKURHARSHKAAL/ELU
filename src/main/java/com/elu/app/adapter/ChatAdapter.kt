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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.tvAvatar.text = if (chat.otherUserName.isNotEmpty()) chat.otherUserName.take(1).uppercase() else "U"
        holder.tvChatName.text = chat.otherUserName.ifEmpty { "Unknown" }
        holder.tvLastMessage.text = chat.lastMessage.ifEmpty { "Say hi 👋" }
        holder.itemView.setOnClickListener { onChatClick(chat) }
    }

    override fun getItemCount(): Int = chats.size
}
