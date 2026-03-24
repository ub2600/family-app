package com.familyapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "todo_lists")
data class TodoList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var name: String = "",

    var icon: String = "📋",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    var createdBy: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    var family: Family? = null,

    @OneToMany(mappedBy = "todoList", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<ListItem> = mutableListOf(),

    val createdAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "list_items")
data class ListItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var text: String = "",

    var completed: Boolean = false,

    var quantity: Int? = null,

    var notes: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    var completedBy: User? = null,

    var completedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    var todoList: TodoList? = null,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
