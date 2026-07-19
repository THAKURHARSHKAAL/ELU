package com.elu.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PKBattleBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_pk_battle, container, false)
        
        view.findViewById<Button>(R.id.btnStartPK).setOnClickListener {
            Toast.makeText(context, "PK Battle Started!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        return view
    }
}