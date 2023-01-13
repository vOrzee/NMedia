package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auxiliary.ConstantValues.emptyPost
import ru.netology.nmedia.auxiliary.ConstantValues.noPhoto
import ru.netology.nmedia.dto.Comment
import ru.netology.nmedia.dto.MediaUpload
//import ru.netology.nmedia.database.AppDbRoom
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    application: Application,
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : AndroidViewModel(application) {
    val data: Flow<PagingData<Post>>
        get() = appAuth
            .authStateFlow
            .flatMapLatest { (myId, _) ->
                repository.data
                    .map { posts ->
                            posts.map { it.copy(ownedByMe = it.authorId == myId) }
                    }
            }.flowOn(Dispatchers.Default)

    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState.Idle)
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    private val edited = MutableLiveData(emptyPost)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val dataComment: LiveData<List<Comment>>
        get() = _dataComment
    private val _dataComment: MutableLiveData<List<Comment>> = MutableLiveData(listOf())

    private val _photo = MutableLiveData(
        PhotoModel(
            edited.value?.attachment?.url?.toUri(),
            edited.value?.attachment?.url?.toUri()?.toFile()
        )
            ?: noPhoto
    )
    val photo: LiveData<PhotoModel>
        get() = _photo

//    val newerCount: LiveData<Int> = data.switchMap {
//        repository.getNewerCount(it.posts.firstOrNull()?.id ?: 0L)
//            .catch { e -> e.printStackTrace() }
//            .asLiveData(Dispatchers.Default)
//    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    init {
        loadPosts()
    }

    fun viewNewPosts() = viewModelScope.launch {
        try {
            repository.showNewPosts()
            _dataState.value = FeedModelState.ShadowIdle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState.Loading
            repository.getAllAsync()
            _dataState.value = FeedModelState.ShadowIdle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState.Refresh
            repository.getAllAsync()
            _dataState.value = FeedModelState.ShadowIdle
        } catch (e: Exception) {
            _dataState.value = FeedModelState.Error
        }
    }

    fun likeById(post: Post) {
        viewModelScope.launch {
            try {
                repository.likeByIdAsync(post)
            } catch (e: Exception) {
                _dataState.value = FeedModelState.Error
            }
        }
    }

    fun getCommentsById(post: Post) {
        viewModelScope.launch {
            try {
                _dataComment.value = repository.getCommentsById(post)
                _dataState.value = FeedModelState.ShadowIdle
            } catch (e: Exception) {
                _dataState.value = FeedModelState.Error
            }
        }
    }

    fun shareById(id: Long) {//пока ничего
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeByIdAsync(id)
                _dataState.value = FeedModelState.Idle
            } catch (e: Exception) {
                _dataState.value = FeedModelState.Error
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun getEditedId(): Long {
        return edited.value?.id ?: 0
    }

    fun getEditedPostImgRes(): String? {
        return edited.value?.attachment?.url
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }

    fun deleteAttachment() {
        edited.value = edited.value?.copy(attachment = null)
    }

    fun save() {
        edited.value?.let { savingPost ->
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.saveAsync(savingPost)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(savingPost, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState.Idle
                } catch (e: Exception) {
                    _dataState.value = FeedModelState.Error
                }
            }
        }
        edited.value = emptyPost
        _photo.value = noPhoto
    }
}