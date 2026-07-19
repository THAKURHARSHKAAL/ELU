package com.elu.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elu.app.adapter.GiftAdapter
import com.elu.app.model.GiftModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GiftBottomSheet(private val onGiftSent: (GiftModel) -> Unit) : BottomSheetDialogFragment() {

    private val gifts = listOf(
        GiftModel("1", "Rose", "🌹", 10),
        GiftModel("2", "Heart", "❤️", 50),
        GiftModel("3", "Ring", "💍", 100),
        GiftModel("4", "Car", "🚗", 500),
        GiftModel("5", "Castle", "🏰", 1000),
        GiftModel("6", "Rocket", "🚀", 5000)
    )
    private var selectedGift: GiftModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_gift, container, false)
        
        val rvGifts = view.findViewById<RecyclerView>(R.id.rvGifts)
        rvGifts.layoutManager = GridLayoutManager(context, 3)
        rvGifts.adapter = GiftAdapter(gifts) { selectedGift = it }

        view.findViewById<Button>(R.id.btnSendGift).setOnClickListener {
            selectedGift?.let {
                onGiftSent(it)
                dismiss()
            }
        }

        return view
    }
}