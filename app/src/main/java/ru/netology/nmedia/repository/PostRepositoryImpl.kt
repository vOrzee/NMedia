package ru.netology.nmedia.repository

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.*
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError


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
            val response = Api.retrofitService.getNewer(id)
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
            val response = Api.retrofitService.getAll()

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
            val response = Api.retrofitService.removeById(id)

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
            val response = Api.retrofitService.save(post)

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

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment = post.copy(attachment =
            Attachment(
                url = media.id,
                type = AttachmentType.IMAGE,
                description = null
            ))
            saveAsync(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = Api.retrofitService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
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
                Api.retrofitService.dislikeById(post.id)
            } else {
                Api.retrofitService.likeById(post.id)

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

    override suspend fun getCommentsById(post: Post) : List<Comment> {
        try {
            val response = Api.retrofitService.getCommentsById(post.id)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insertCommentsPost(body.toEntity())
            return body
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
