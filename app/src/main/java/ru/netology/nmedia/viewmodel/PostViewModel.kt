package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.auxiliary.ConstantValues.emptyPost
//import ru.netology.nmedia.database.AppDbRoom
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

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

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
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
                        .map { if (it.id == id) value else it }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(onFailure = true))
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
                _data.postValue(FeedModel(posts = newState, onSuccess = true))
            }

            override fun onError(e: Exception) {
                loadPosts()
                _data.postValue(FeedModel(onFailure = true))
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
            repository.saveAsync(editedPost, object : PostRepository.Callback<Unit> {
                override fun onSuccess(value: Unit) {
                    _postCreated.postValue(Unit)
                    _data.postValue(FeedModel(posts = newStatePosts, onSuccess = true))
                }

                override fun onError(e: Exception) {
                    loadPosts()
                    _data.postValue(FeedModel(onFailure = true))
                }
            })
        }
        edited.value = emptyPost
    }
}