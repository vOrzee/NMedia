package ru.netology.nmedia.dto

import android.icu.text.CaseMap.Title

data class Post(
    val id: Long,
    val title: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val sharedByMe: Boolean = false,
    val viewedByMe: Boolean = false,
    val countLikes: Int = 9_999,
    val countShared: Int = 999,
    val countViews: Int = 99_999,
    val videoUrl:String? = null
)
