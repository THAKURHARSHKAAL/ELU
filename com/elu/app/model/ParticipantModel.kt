package com.elu.app.model

data class ParticipantModel(
    val uid: String = "",
    val name: String = "",
    val avatar: String = "",
    val seatIndex: Int = -1, // -1 means not sitting
    val isHost: Boolean = false
)
