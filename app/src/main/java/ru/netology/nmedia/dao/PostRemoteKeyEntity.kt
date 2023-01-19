package ru.netology.nmedia.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PostRemoteKeyEntity (
    @PrimaryKey
    val type: KeyType,
    val idPost: Long,
) {
    enum class KeyType {
        AFTER,
        BEFORE,
    }
}