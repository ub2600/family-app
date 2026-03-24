package com.familyapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

enum class MessageType {
    TEXT, IMAGE, VIDEO, VOICE
}

@Entity
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    var content: String? = null,

    var mediaUrl: String? = null,

    @Enumerated(EnumType.STRING)
    var messageType: MessageType = MessageType.TEXT,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    var sender: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null,

    val timestamp: LocalDateTime = LocalDateTime.now()
)
