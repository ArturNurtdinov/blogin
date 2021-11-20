package ru.spbstu.common.domain

data class Blog(
    val id: Long,
    val name: String,
    val date: String,
    val post: String,
    val avatarUrl: String,
    val photoUrl: String?
)
