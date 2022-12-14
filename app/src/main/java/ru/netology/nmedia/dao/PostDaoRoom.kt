package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostDaoRoom {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(vararg post: PostEntity)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun updateContentById(id: Long, content: String)

    fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
        UPDATE PostEntity SET
        countLikes = countLikes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Long)

    @Query(
        """
        UPDATE PostEntity SET
        countShared = countShared + CASE WHEN sharedByMe THEN -1 ELSE 1 END,
        sharedByMe = CASE WHEN sharedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun shareById(id: Long)

}