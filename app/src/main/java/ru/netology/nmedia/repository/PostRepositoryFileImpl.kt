package ru.netology.nmedia.repository
/*
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(private val context: Context) : PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val filename = "posts.json"
    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(filename)
        if (file.exists()) {
            context.openFileInput(filename).bufferedReader().use {
                try {
                    posts = gson.fromJson(it, type)
                    data.value = posts
                } catch (e: Exception) {
                    data.value = posts
                }
            }
        } else {
            sync()
        }
    }

    private fun sync() {
        context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
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