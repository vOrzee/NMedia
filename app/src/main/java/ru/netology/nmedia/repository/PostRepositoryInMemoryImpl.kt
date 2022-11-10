package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostRepository

class PostRepositoryInMemoryImpl: PostRepository {
    private var post = Post(
        id = 1,
        title = "Нетология. Университет интернет-профессий будущего",
        content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
        published = "21 мая в 18:36"
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() {
        val countLikes = if (post.likedByMe) (post.countLikes - 1) else (post.countLikes + 1)
        post = post.copy(likedByMe = !post.likedByMe, countLikes = countLikes)
        data.value = post
    }
    override fun share() {
        val countShared = if (post.sharedByMe) (post.countShared - 1) else (post.countShared + 1)
        post = post.copy(sharedByMe = !post.sharedByMe, countShared = countShared)
        data.value = post
    }
}