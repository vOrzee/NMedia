package ru.netology.nmedia.dto

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 9_999,
    val sharedByMe: Boolean = false,
    val countShared: Int = 999,
    val viewedByMe: Boolean = false,
    val countViews: Int = 99_999,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)

data class Ad(
    override val id:Long,
    val image:String,
) : FeedItem

data class TimingSeparator(
    override val id:Long,
    val text:String,
) : FeedItem

enum class AttachmentType {
    IMAGE,
    VIDEO
}