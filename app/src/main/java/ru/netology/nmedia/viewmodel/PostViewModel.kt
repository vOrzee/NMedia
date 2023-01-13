package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.adapters.PostAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auxiliary.ConstantValues.emptyPost
import ru.netology.nmedia.auxiliary.ConstantValues.noPhoto
import ru.netology.nmedia.auxiliary.FloatingValue.agoToText
import ru.netology.nmedia.dto.*
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import java.time.OffsetDateTime.now
import javax.inject.Inject
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    application: Application,
    private val repository: PostRepository,
    appAuth: AppAuth,
) : AndroidViewModel(application) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val cached: Flow<PagingData<FeedItem>> = repository
        .data
        .map { pagingData ->
            pagingData.insertSeparators(
                generator = { prev, next ->
                    val currentTime = now().toEpochSecond()
                    if ((prev is Post && next is Post)) {
                        val howOlderPrev = agoToText(
                            (currentTime - prev.published.toLong()).toInt(),
                            inLongAgoTextDescription = "Давно",
                            inTodayTextDescription = "Сегодня",
                            inHourTextDescription = "Не прошло и часа",
                            inMinuteTextDescription = "Только что",
                        )
                        val howOlderNext = agoToText(
                            (currentTime - next.published.toLong()).toInt(),
                            inLongAgoTextDescription = "Давно",
                            inTodayTextDescription = "Сегодня",
                            inHourTextDescription = "Не прошло и часа",
                            inMinuteTextDescription = "Только что",
                        )
                        when {
                            (howOlderPrev != howOlderNext) -> {
                                TimingSeparator(
                                    Random.nextLong(),
                                    howOlderNext
                                )
                            }
                            (prev.id.rem(5) == 0L) -> {
                                Ad(
                                    Random.nextLong(),
                                    "figma.jpg"
                                )
                            }
                            else -> null
                        }
                    } else {
                        if (prev is Post) {
                            TimingSeparator(Random.nextLong(),"Предыдущий")
                        } else if (next is Post) {
                            TimingSeparator(Random.nextLong(), "Следующий")
                        } else {
                            TimingSeparator(Random.nextLong(), "Иначный")
                        }
                    }
                }
            )
        }
        .cachedIn(viewModelScope)

    @RequiresApi(Build.VERSION_CODES.O)
    val data: Flow<PagingData<FeedItem>> = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    if (post is Post) {
                        post.copy(ownedByMe = post.authorId == myId)
                    } else {
                        post
                    }
                }
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

//    val dataPost: LiveData<Post>
//        get() = _dataPost
//    private val _dataPost: MutableLiveData<Post> = MutableLiveData(emptyPost)

    private val _photo = MutableLiveData(
        PhotoModel(
            edited.value?.attachment?.url?.toUri(),
            edited.value?.attachment?.url?.toUri()?.toFile()
        )
    )
    val photo: LiveData<PhotoModel>
        get() = _photo

    val newerCount = repository.getNewerCount()
        .catch { e -> e.printStackTrace() }

    init {
        loadPosts()
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun viewNewPosts() = viewModelScope.launch {
        try {
            repository.showNewPosts()
            loadPosts()
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

    fun refreshPosts(adapter: PostAdapter) {
        try {
            _dataState.value = FeedModelState.Refresh
            adapter.refresh()
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

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
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

//    fun getPostById(id: Long) = repository.getById(id)

//    fun shareById(id: Long) {  }

//    fun getEditedPostImgRes(): String? { //todo
//        return edited.value?.attachment?.url
//    }

//    fun deleteAttachment() { //todo
//        edited.value = edited.value?.copy(attachment = null)
//    }
}