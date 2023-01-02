package ru.netology.nmedia.auxiliary

import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel

object ConstantValues {
    const val POST_KEY = "POST_KEY"
    val emptyPost = Post(
        id = 0,
        authorId = 0,
        content = "",
        author = "Нетология",
        likes = 0,
        countShared = 0,
        countViews = 0,
        published = ""
    )
    val noPhoto = PhotoModel()
}