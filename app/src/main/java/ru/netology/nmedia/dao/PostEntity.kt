package ru.netology.nmedia.dao

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
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
    @Embedded
    var attachment: AttachmentEmbeddable?,
    val isNew: Boolean = false,
) {

    fun toDto() = Post(
        id, authorId, author, authorAvatar, content, published, likedByMe, likes,
        sharedByMe, countShared, viewedByMe, countViews, attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes,
                dto.sharedByMe, dto.countShared, dto.viewedByMe, dto.countViews, AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}

data class AttachmentEmbeddable(
    var url: String,
    var description: String?,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, description, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.description, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(isNew: Boolean = false): List<PostEntity> = map(PostEntity::fromDto)
    .map { it.copy(isNew = isNew) }