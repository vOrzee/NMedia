package ru.netology.nmedia.repository

import androidx.paging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.*
import ru.netology.nmedia.database.AppDbRoom
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import javax.inject.Inject



class PostRepositoryImpl @Inject constructor(
    private val dao: PostDaoRoom,
    private val daoKey: PostRemoteKeyDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDbRoom,
) : PostRepository {

    private val newerPostsId = mutableListOf<Long>()

    @OptIn(ExperimentalPagingApi::class)
    override val data:Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = true),
        pagingSourceFactory = { dao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            postDao = dao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override fun getNewerCount(): Flow<Int> = flow {
        while (true) {
            val response = apiService.getNewer(daoKey.max() ?: 0)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            body.forEach {
                newerPostsId.add(it.id)
            }
            emit(body.size)
            delay(3_000L)
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
            val response = apiService.getAll()

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
            val response = apiService.removeById(id)

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
            val response = apiService.save(post)

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
            val postWithAttachment = post.copy(
                attachment =
                Attachment(
                    url = media.id,
                    type = AttachmentType.IMAGE,
                    description = null
                )
            )
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

            val response = apiService.upload(media)
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

    override suspend fun likeByIdAsync(id: Long, likedByMe: Boolean) {
        try {
            val response = if (likedByMe) {
                dao.likeById(id)
                apiService.dislikeById(id)
            } else {
                dao.likeById(id)
                apiService.likeById(id)
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

    override suspend fun getCommentsById(post: Post): List<Comment> {
        try {
            val response = apiService.getCommentsById(post.id)

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

    override suspend fun getById(id: Long) : Post {
        //val result = apiService.getById(id)
        return dao.getPostById(id).toDto()//result.body() ?: emptyPost

    }

    override suspend fun edit(post: Post) {
        saveAsync(post)
    }
}
