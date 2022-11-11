package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostRepository

class PostRepositoryInMemoryImpl : PostRepository {
    private var posts = listOf(
        Post(
            id = 1,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "21 мая в 18:36"
        ),
        Post(
            id = 2,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "18 сентября в 10:12"
        ),
        Post(
            id = 3,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Третий пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "19 сентября в 12:12"
        ),
        Post(
            id = 4,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Четвёртый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "20 сентября в 12:12"
        ),
        Post(
            id = 5,
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
}