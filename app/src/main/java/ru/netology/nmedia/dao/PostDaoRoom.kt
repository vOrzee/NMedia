package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDaoRoom {
    @Query("SELECT * FROM PostEntity WHERE isNew = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query(
        """
        SELECT * FROM CommentEntity
        WHERE postId = :id
        """
    )
    fun getCommentsByPost(id: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommentsPost(comment: List<CommentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun updateContentById(id: Long, content: String)

    @Query(
        """
        UPDATE PostEntity SET isNew = 0
        WHERE isNew = 1
        """
    )
    suspend fun showNewPosts()

    suspend fun save(post: PostEntity) =
        if (post.id == 0L) insert(post) else updateContentById(post.id, post.content)

    @Query(
        """
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

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