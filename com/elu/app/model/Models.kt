package com.elu.app.model

// Firestore document: users/{uid}
data class UserModel(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var diamonds: Long = 0,
    var points: Long = 0,
    var bio: String = "",
    var createdAt: Long = 0
)

// Firestore document: rooms/{roomId}
data class RoomModel(
    var roomId: String = "",
    var roomShortId: String = "",
    var title: String = "",
    var emoji: String = "🎮",
    var hostUid: String = "",
    var hostName: String = "",
    var participantCount: Long = 0,
    var createdAt: Long = 0
)

// Firestore document: rooms/{roomId}/messages/{messageId}
// and chats/{chatId}/messages/{messageId}
data class MessageModel(
    var messageId: String = "",
    var senderUid: String = "",
    var senderName: String = "",
    var text: String = "",
    var timestamp: Long = 0,
    var isGift: Boolean = false,
    var giftIcon: String = ""
)

// Firestore document: moments/{momentId}
data class MomentModel(
    var momentId: String = "",
    var authorUid: String = "",
    var authorName: String = "",
    var text: String = "",
    var createdAt: Long = 0
)

// Firestore document: chats/{chatId}
// chatId is built as the two participant uids sorted + joined with "_"
data class ChatModel(
    var chatId: String = "",
    var participantUids: List<String> = emptyList(),
    var lastMessage: String = "",
    var lastMessageTimestamp: Long = 0,
    var isGroup: Boolean = false,
    var groupName: String = "",
    // Not stored - filled in at runtime for display
    var otherUserName: String = "",
    var otherUserUid: String = ""
)

data class NotificationModel(
    var id: String = "",
    var type: String = "system", // activity, system
    var title: String = "",
    var content: String = "",
    var timestamp: Long = 0,
    var icon: String = "🔔"
)
