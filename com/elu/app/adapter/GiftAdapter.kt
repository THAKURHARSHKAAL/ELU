package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.R
import com.elu.app.model.GiftModel

class GiftAdapter(
    private val gifts: List<GiftModel>,
    private val onGiftSelected: (GiftModel) -> Unit
) : RecyclerView.Adapter<GiftAdapter.ViewHolder>() {

    private var selectedPosition = -1

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvGiftIcon)
        val tvName: TextView = view.findViewById(R.id.tvGiftName)
        val tvPrice: TextView = view.findViewById(R.id.tvGiftPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gift, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gift = gifts[position]
        holder.tvIcon.text = gift.icon
        holder.tvName.text = gift.name
        holder.tvPrice.text = "💎 ${gift.price}"

        holder.itemView.alpha = if (selectedPosition == position) 1.0f else 0.6f
        holder.itemView.scaleX = if (selectedPosition == position) 1.1f else 1.0f
        holder.itemView.scaleY = if (selectedPosition == position) 1.1f else 1.0f

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onGiftSelected(gift)
        }
    }

    override fun getItemCount() = gifts.size
}