package ru.netology.nmedia.repository
/*
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositorySharedPrefsImpl(
    context: Context,
) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key, null)?.let {
            posts = gson.fromJson(it, type)
            data.value = posts
        }
    }

    private fun sync() {
        with(prefs.edit()) {
            putString(key, gson.toJson(posts))
            apply()
        }
    }

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else {
                val likes =
                    if (it.likedByMe) (it.likes - 1) else (it.likes + 1)
                it.copy(likedByMe = !it.likedByMe, likes = likes)
            }
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else {
                val likes =
                    if (it.sharedByMe) (it.countShared - 1) else (it.countShared + 1)
                it.copy(sharedByMe = !it.sharedByMe, countShared = likes)
            }
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
        sync()
    }

    override fun edit(post: Post) {
        save(post)
    }

    override fun save(post: Post) {
        if (post.id == 0L && post.content.isNotEmpty()) {
            posts = posts + listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    published = "now",
                    likedByMe = false,
                    sharedByMe = false,
                    viewedByMe = false
                )
            )
            data.value = posts
            sync()
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
        sync()
    }
}
*/