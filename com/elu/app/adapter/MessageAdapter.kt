package com.elu.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.elu.app.R
import com.elu.app.model.MessageModel

class MessageAdapter(
    private val messages: List<MessageModel>,
    private val currentUid: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_SENT = 1
        const val TYPE_RECEIVED = 2
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
        val ivGif: ImageView = view.findViewById(R.id.ivGif)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderUid == currentUid) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutRes = if (viewType == TYPE_SENT) R.layout.item_message_sent else R.layout.item_message_received
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val vh = holder as MessageViewHolder
        
        if (message.isGift) {
            vh.tvMessage.visibility = View.VISIBLE
            vh.ivGif.visibility = View.GONE
            vh.tvMessage.text = "${message.senderName} ${message.text}"
            vh.tvMessage.setTextColor(vh.tvMessage.context.getColor(R.color.elu_pink))
        } else if (message.text.startsWith("http") && (message.text.contains(".gif") || message.text.contains("giphy"))) {
            vh.tvMessage.visibility = View.GONE
            vh.ivGif.visibility = View.VISIBLE
            Glide.with(vh.ivGif.context).asGif().load(message.text).into(vh.ivGif)
        } else {
            vh.tvMessage.visibility = View.VISIBLE
            vh.ivGif.visibility = View.GONE
            vh.tvMessage.text = message.text
            vh.tvMessage.setTextColor(vh.tvMessage.context.getColor(R.color.text_dark))
        }
    }

    override fun getItemCount(): Int = messages.size
}
