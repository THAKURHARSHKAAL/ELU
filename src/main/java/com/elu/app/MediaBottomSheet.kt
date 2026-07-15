package com.elu.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MediaBottomSheet(private val onMediaSelected: (String) -> Unit) : BottomSheetDialogFragment() {

    private val emojis = listOf(
        "😊", "😂", "🥰", "😍", "🤩", "😎", "🤔", "😴",
        "🔥", "✨", "💯", "🎉", "❤️", "🌹", "🎁", "🍺",
        "👏", "🙌", "🙏", "💪", "👍", "👎", "👋", "🤝"
    )

    private val gifs = listOf(
        "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHJqZ3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6JmVwPXYxX2ludGVybmFsX2dpZl9ieV9pZCZjdD1n/3o7TKQHqO5XJ8yX0Y0/giphy.gif",
        "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHJqZ3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6JmVwPXYxX2ludGVybmFsX2dpZl9ieV9pZCZjdD1n/l0HlHFRbmaZtBRhXG/giphy.gif",
        "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHJqZ3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6JmVwPXYxX2ludGVybmFsX2dpZl9ieV9pZCZjdD1n/3o7TKVUn7iM8FMEU24/giphy.gif",
        "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHJqZ3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6Z3R6JmVwPXYxX2ludGVybmFsX2dpZl9ieV9pZCZjdD1n/l41lTfhu89rU8F9C0/giphy.gif"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_media, container, false)
        
        val rvEmoji = view.findViewById<RecyclerView>(R.id.rvEmoji)
        rvEmoji.layoutManager = GridLayoutManager(context, 6)
        rvEmoji.adapter = EmojiAdapter(emojis) { emoji ->
            onMediaSelected(emoji)
            dismiss()
        }

        val rvGif = view.findViewById<RecyclerView>(R.id.rvGif)
        rvGif.layoutManager = GridLayoutManager(context, 2)
        rvGif.adapter = GifAdapter(gifs) { gif ->
            onMediaSelected(gif)
            dismiss()
        }

        return view
    }

    private class EmojiAdapter(private val list: List<String>, private val onClick: (String) -> Unit) : 
        RecyclerView.Adapter<EmojiAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) { val tv = v as TextView }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = TextView(parent.context).apply {
                textSize = 24f
                setPadding(12, 12, 12, 12)
                gravity = android.view.Gravity.CENTER
            }
            return VH(tv)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tv.text = list[position]
            holder.tv.setOnClickListener { onClick(list[position]) }
        }
        override fun getItemCount() = list.size
    }

    private class GifAdapter(private val list: List<String>, private val onClick: (String) -> Unit) : 
        RecyclerView.Adapter<GifAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) { val iv = v as ImageView }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val iv = ImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(4, 4, 4, 4)
            }
            return VH(iv)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            Glide.with(holder.iv.context).asGif().load(list[position]).into(holder.iv)
            holder.iv.setOnClickListener { onClick(list[position]) }
        }
        override fun getItemCount() = list.size
    }
}
