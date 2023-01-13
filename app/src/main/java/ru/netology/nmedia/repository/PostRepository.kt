package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.*

interface PostRepository {

    val data: Flow<PagingData<FeedItem>>

    fun getNewerCount(): Flow<Int>
    suspend fun showNewPosts()
    fun shareById(id: Long)
    suspend fun edit(post: Post)

    suspend fun getAllAsync()
    suspend fun getById(id: Long) : Post
    suspend fun removeByIdAsync(id: Long)
    suspend fun saveAsync(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun likeByIdAsync(post: Post)
    suspend fun getCommentsById(post: Post) : List<Comment>
    suspend fun upload(upload: MediaUpload): Media
}