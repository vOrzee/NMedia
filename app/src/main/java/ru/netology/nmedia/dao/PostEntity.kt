package ru.netology.nmedia.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
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
) {

    fun toPost() = Post(
        id, title, content, published, likedByMe, countLikes,
        sharedByMe, countShared, viewedByMe, countViews, videoUrl
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id, dto.title, dto.content, dto.published, dto.likedByMe, dto.countLikes,
                dto.sharedByMe, dto.countShared, dto.viewedByMe, dto.countViews, dto.videoUrl
            )
    }
}