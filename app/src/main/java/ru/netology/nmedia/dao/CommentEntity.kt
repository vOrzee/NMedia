package ru.netology.nmedia.dao

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Comment
import ru.netology.nmedia.dto.Post

@Entity
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val postId: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
) {

    fun toDto() = Comment(
        id, postId, authorId, author, authorAvatar, content, published, likedByMe, likes,
    )

    companion object {
        fun fromDto(dto: Comment) =
            CommentEntity(
                dto.id, dto.postId, dto.authorId, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes,
            )
    }
}

fun List<CommentEntity>.toDto(): List<Comment> = map(CommentEntity::toDto)
fun List<Comment>.toEntity(): List<CommentEntity> = map(CommentEntity::fromDto)