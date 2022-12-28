package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.auxiliary.ConstantValues.emptyPost
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit


class PostRepositoryImpl: PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://192.168.0.204:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let {
                it.body?.string()
            }
            ?.let {
                gson.fromJson(it, typeToken.type)
            } ?: emptyList()
    }

    override fun likeById(id: Long) {
        val requestInput: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()
        val post: Post = client.newCall(requestInput)
            .execute()
            .let {
                gson.fromJson(it.body?.string(), Post::class.java)
            } ?: emptyPost
        val requestOutput: Request = if (post.likedByMe) {
            Request.Builder()
                .delete(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()
        } else {
            Request.Builder()
                .post(gson.toJson(post).toRequestBody(jsonType))
                .url("${BASE_URL}/api/slow/posts/$id/likes")
                .build()
        }
        client.newCall(requestOutput)
            .execute()
            .close()
    }

    override fun shareById(id: Long) {
        //TODO add when server support this
        println("shared")
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun edit(post: Post) {
        save(post)
    }
}
