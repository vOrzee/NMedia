package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post


class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId: Long = 1L
    private var posts = listOf(
        Post(
            id = nextId++,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "21 мая в 18:36"
        ),
        Post(
            id = nextId++,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "18 сентября в 10:12",
            videoUrl = "https://www.youtube.com/watch?v=ir0vFRaQ-Hw"
        ),
        Post(
            id = nextId++,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Третий пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "19 сентября в 12:12"
        ),
        Post(
            id = nextId++,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Четвёртый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "20 сентября в 12:12"
        ),
        Post(
            id = nextId++,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Пятый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "21 сентября в 12:12"
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data
    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else {
                val countLikes =
                    if (it.likedByMe) (it.countLikes - 1) else (it.countLikes + 1)
                it.copy(likedByMe = !it.likedByMe, countLikes = countLikes)
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else {
                val countLikes =
                    if (it.sharedByMe) (it.countShared - 1) else (it.countShared + 1)
                it.copy(sharedByMe = !it.sharedByMe, countShared = countLikes)
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun edit(post: Post) {
        save(post)
    }

    override fun save(post: Post) {
        if (post.id == 0L && post.content.isNotEmpty()) {
            posts = posts + listOf(
                post.copy(
                    id = nextId++,
                    title = "Me",
                    published = "now",
                    likedByMe = false,
                    sharedByMe = false,
                    viewedByMe = false
                )
            )
            data.value = posts
            return
        }

        posts = posts.map {
            if (it.id != post.id) it else it.copy(content = post.content)
        }
        data.value = posts
    }
}