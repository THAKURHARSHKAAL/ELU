package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.R
import com.elu.app.model.ParticipantModel

class MemberAdapter(
    private val members: List<ParticipantModel>,
    private val isHost: Boolean,
    private val onKick: (ParticipantModel) -> Unit
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMemberName)
        val btnKick: Button = view.findViewById(R.id.btnKickMember)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.tvName.text = member.name
        holder.btnKick.visibility = if (isHost && !member.isHost) View.VISIBLE else View.GONE
        holder.btnKick.setOnClickListener { onKick(member) }
    }

    override fun getItemCount() = members.size
}