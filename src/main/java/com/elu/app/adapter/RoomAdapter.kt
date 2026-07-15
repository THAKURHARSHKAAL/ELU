package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.R
import com.elu.app.model.RoomModel

class RoomAdapter(
    private val rooms: List<RoomModel>,
    private val onRoomClick: (RoomModel) -> Unit
) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomEmoji: TextView = view.findViewById(R.id.tvRoomEmoji)
        val tvRoomTitle: TextView = view.findViewById(R.id.tvRoomTitle)
        val tvRoomHost: TextView = view.findViewById(R.id.tvRoomHost)
        val tvParticipants: TextView = view.findViewById(R.id.tvParticipants)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = rooms[position]
        holder.tvRoomEmoji.text = room.emoji
        holder.tvRoomTitle.text = room.title
        holder.tvRoomHost.text = "Hosted by ${room.hostName}"
        holder.tvParticipants.text = "${room.participantCount} 👥"
        holder.itemView.setOnClickListener { onRoomClick(room) }
    }

    override fun getItemCount(): Int = rooms.size
}
