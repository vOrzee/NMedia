package ru.netology.nmedia.dto

import android.icu.text.CaseMap.Title

data class Post(
    val id: Long,
    val title: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    var sharedByMe: Boolean = false,
    var viewedByMe: Boolean = false,
    var countLikes: Int = 9_999,
    var countShared: Int = 999,
    var countViews: Int = 99_999
)
