package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.PostDaoRoom
import ru.netology.nmedia.dao.PostEntity
import ru.netology.nmedia.dao.toDto
import ru.netology.nmedia.dao.toEntity
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import kotlin.coroutines.EmptyCoroutineContext


class PostRepositoryImpl(private val dao: PostDaoRoom) : PostRepository {

    private val newerPostsId = mutableListOf<Long>()

    override val data = dao.getAll()
        .map {
            it.filter { postEntity ->
                !postEntity.isNew
            }
        }
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.retrofitService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity(isNew = true))
            body.forEach {
                newerPostsId.add(it.id)
            }
            emit(body.size)
        }
    }

    override suspend fun showNewPosts() {
        dao.showNewPosts()
    }

    override fun shareById(id: Long) {
        //TODO add when server support this
        println("shared")
    }

    override suspend fun getAllAsync() {

        try {
            val response = PostsApi.retrofitService.getAll()

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeByIdAsync(id: Long) {
        try {
            val response = PostsApi.retrofitService.removeById(id)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveAsync(post: Post) {
        try {
            val response = PostsApi.retrofitService.save(post)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.save(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeByIdAsync(post: Post) {
        dao.likeById(post.id)
        try {
            val response = if (post.likedByMe) {
                PostsApi.retrofitService.dislikeById(post.id)
            } else {
                PostsApi.retrofitService.likeById(post.id)

            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun edit(post: Post) {
        saveAsync(post)
    }
}
