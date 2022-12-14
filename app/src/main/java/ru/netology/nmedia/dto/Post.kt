package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val title: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val countLikes: Int = 9_999,
    val sharedByMe: Boolean = false,
    val countShared: Int = 999,
    val viewedByMe: Boolean = false,
    val countViews: Int = 99_999,
    val videoUrl: String? = null
)
