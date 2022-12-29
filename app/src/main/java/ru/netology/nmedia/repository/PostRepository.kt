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

    fun getAllAsync(callback: Callback<List<Post>>)
    fun removeByIdAsync(id: Long, callback: Callback<Unit>)
    fun saveAsync(post: Post, callback: Callback<Unit>)
    fun likeByIdAsync(post: Post, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(value: T) {}
        fun onError(e: Exception) {}
    }
}