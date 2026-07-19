package com.elu.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RoomSettingsBottomSheet(
    private val currentTopic: String,
    private val isLocked: Boolean,
    private val onSettingsUpdated: (String, Boolean) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_room_settings, container, false)
        
        val etTopic = view.findViewById<EditText>(R.id.etRoomTopic)
        val switchLock = view.findViewById<Switch>(R.id.switchLock)
        
        etTopic.setText(currentTopic)
        switchLock.isChecked = isLocked

        view.findViewById<Button>(R.id.btnUpdateSettings).setOnClickListener {
            onSettingsUpdated(etTopic.text.toString(), switchLock.isChecked)
            dismiss()
        }

        return view
    }
}