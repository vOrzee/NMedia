package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.BuildConfig.BASE_URL
import ru.netology.nmedia.auxiliary.ConstantValues.emptyPost
//import ru.netology.nmedia.database.AppDbRoom
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

class PostViewModel(application: Application) : AndroidViewModel(application) {
    //    private val repository : PostRepository = PostRepositoryRoomImpl(
//        AppDbRoom.getInstance(application).postDaoRoom()
//    )
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(emptyPost)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated


    init {
        loadPosts()
    }

    fun renameUrl(baseUrl: String, path: String, nameResource: String): String {
        return "$baseUrl/$path/$nameResource"
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(
                    FeedModel(
                        posts = value.map {
                            it.copy(
                                authorAvatar =
                                if (!it.authorAvatar.isNullOrBlank()) {
                                    renameUrl(BASE_URL, "avatars", it.authorAvatar)
                                } else {
                                    null
                                },
                                attachment = if (it.attachment != null) {
                                    it.attachment.copy(
                                        url = renameUrl(
                                            BASE_URL,
                                            "images",
                                            it.attachment.url
                                        )
                                    )
                                } else {
                                    null
                                }
                            )
                        },
                        empty = value.isEmpty()
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun likeById(id: Long) {
        val post = data.value?.posts?.find { it.id == id } ?: emptyPost

        repository.likeByIdAsync(post, object : PostRepository.Callback<Post> {
            override fun onSuccess(value: Post) {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .map {
                            if (it.id == id) value.copy(
                                authorAvatar =
                                if (!value.authorAvatar.isNullOrBlank()) {
                                    renameUrl(BASE_URL, "avatars", value.authorAvatar)
                                } else {
                                    null
                                }, attachment =
                                if (value.attachment != null) {
                                    value.attachment.copy(
                                        url = renameUrl(
                                            BASE_URL,
                                            "images",
                                            value.attachment.url
                                        )
                                    )
                                } else {
                                    null
                                }
                            )
                            else it
                        }, onFailure = false
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(
                    posts = data.value?.posts ?: emptyList(), onFailure = true)
                )
            }
        })
    }

    fun shareById(id: Long) {//пока ничего
    }

    fun removeById(id: Long) {
        val newState = _data.value?.posts.orEmpty()
            .filter { it.id != id }
        _data.postValue(FeedModel(posts = newState, loading = true))
        repository.removeByIdAsync(id, object : PostRepository.Callback<Unit> {
            override fun onSuccess(value: Unit) {
                _data.postValue(FeedModel(posts = newState ?: emptyList(), onSuccess = true, onFailure = false))
                if (newState.isEmpty()) loadPosts()
            }

            override fun onError(e: Exception) {
                loadPosts()
                _data.postValue(FeedModel(onFailure = true, onSuccess = false))
            }
        })
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun getEditedId(): Long {
        return edited.value?.id ?: 0
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }

    fun save() {
        edited.value?.let { editedPost ->
            val newStatePosts = _data.value?.posts.orEmpty()
                .map { if (it.id == editedPost.id) editedPost else it }
            repository.saveAsync(editedPost, object : PostRepository.Callback<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                    _data.postValue(FeedModel(posts = newStatePosts, onSuccess = true, onFailure = false))
                }

                override fun onError(e: Exception) {
                    loadPosts()
                    _data.postValue(FeedModel(onFailure = true, onSuccess = false))
                }
            })
        }
        edited.value = emptyPost
    }
}