package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var sharedByMe: Boolean = false,
    var viewedByMe: Boolean = false,
    var countLikes: Int = 9_999,
    var countShared: Int = 999,
    var countViews: Int = 99_999
)
