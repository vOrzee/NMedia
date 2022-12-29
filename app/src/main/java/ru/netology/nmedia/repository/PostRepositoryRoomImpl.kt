package ru.netology.nmedia.repository
/*
import androidx.lifecycle.Transformations
import ru.netology.nmedia.dao.PostDaoRoom
import ru.netology.nmedia.dao.PostEntity
import ru.netology.nmedia.dto.Post

class PostRepositoryRoomImpl(
    private val daoRoom: PostDaoRoom,
) : PostRepository {
    override fun getAll() = Transformations.map(daoRoom.getAll()) { list ->
        if (list.isEmpty()) {

            daoRoom.insert(
                PostEntity(
                    id = 1,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
                    published = "21 мая в 18:36"
                ),
                PostEntity(
                    id = 2,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
                    published = "18 сентября в 10:12"
                ),
                PostEntity(
                    id = 3,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Третий пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
                    published = "19 сентября в 12:12"
                ),
                PostEntity(
                    id = 4,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Четвёртый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
                    published = "20 сентября в 12:12"
                ),
                PostEntity(
                    id = 5,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Пятый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
                    published = "21 сентября в 12:12",
                    videoUrl = "https://www.youtube.com/watch?v=ir0vFRaQ-Hw"
                )
            )
        }

        list.map {
            it.toPost()
        }
    }

    override fun likeById(id: Long) {
        daoRoom.likeById(id)
    }

    override fun shareById(id: Long) {
        daoRoom.shareById(id)
    }

    override fun save(post: Post) {
        daoRoom.save(PostEntity.fromDto(post))
    }

    override fun removeById(id: Long) {
        daoRoom.removeById(id)
    }

    override fun edit(post: Post) {
        save(post)
    }

} */