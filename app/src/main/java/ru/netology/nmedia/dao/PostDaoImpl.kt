package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {


    private fun map(cursor: Cursor):Post {
        with(cursor){
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                title = getString(getColumnIndexOrThrow(PostColumns.COLUMN_TITLE)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) !=0,
                countLikes = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
//                countShared = getInt(getColumnIndexOrThrow(PostColumns.)),
//                countViews = getInt(getColumnIndexOrThrow(PostColumns.)),
//                videoUrl = getString(getColumnIndexOrThrow(PostColumns.))
            )
        }
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if(post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            //TODO: remove hardcore values
            put(PostColumns.COLUMN_TITLE, "Me")
            put(PostColumns.COLUMN_CONTENT,post.content)
            put(PostColumns.COLUMN_PUBLISHED,"now")
        }
        val id = db.replace(PostColumns.TABLE,null,values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
                UPDATE posts SET
                        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
                        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
                WHERE id = ?;
                """.trimIndent(), arrayOf(id)
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

}