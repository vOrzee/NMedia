package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    //fun getAll(): LiveData<List<Post>>
    fun getAll(): List<Post>
    fun likeById(post: Post) :Post
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun edit(post: Post)
    fun save(post: Post)
}