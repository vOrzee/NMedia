package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {


    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                authorId = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_TITLE)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                likes = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                sharedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) != 0,
                countShared = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARES)),
                viewedByMe = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEW_BY_ME)) != 0,
                countViews = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEWS)),
            )
        }
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE_NAME,
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
        if (posts.isEmpty()) {
            db.execSQL(
                PostColumns.DEFAULT_POSTS
            )
            getAll()
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {

            if (post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            if (post.author.isNotBlank()) {
                put(PostColumns.COLUMN_TITLE, post.author)
            }
            if (post.published.isNotBlank()) {
                put(PostColumns.COLUMN_PUBLISHED, post.published)
            }
            if (post.content.isNotBlank()) {
                put(PostColumns.COLUMN_CONTENT, post.content)
            }
            put(PostColumns.COLUMN_LIKED_BY_ME, post.likedByMe)
            put(PostColumns.COLUMN_LIKES, post.likes)
            put(PostColumns.COLUMN_SHARE_BY_ME, post.sharedByMe)
            put(PostColumns.COLUMN_SHARES, post.countShared)
            put(PostColumns.COLUMN_VIEW_BY_ME, post.viewedByMe)
            put(PostColumns.COLUMN_VIEWS, post.countViews)
            //put(PostColumns.COLUMN_VIDEO_URL, post.videoUrl)
        }
        val id = db.replace(PostColumns.TABLE_NAME, null, values)
        db.query(
            PostColumns.TABLE_NAME,
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
                UPDATE ${PostColumns.TABLE_NAME} SET
                    ${PostColumns.COLUMN_LIKES} = ${PostColumns.COLUMN_LIKES} + CASE WHEN ${PostColumns.COLUMN_LIKED_BY_ME} THEN -1 ELSE 1 END,
                    ${PostColumns.COLUMN_LIKED_BY_ME} = CASE WHEN ${PostColumns.COLUMN_LIKED_BY_ME} THEN 0 ELSE 1 END
                WHERE id=?
            """.trimIndent(),
            arrayOf(id)
        )

    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE_NAME,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            """
                UPDATE ${PostColumns.TABLE_NAME} SET
                    ${PostColumns.COLUMN_SHARES} = ${PostColumns.COLUMN_SHARES} + CASE WHEN ${PostColumns.COLUMN_SHARE_BY_ME} THEN -1 ELSE 1 END,
                    ${PostColumns.COLUMN_SHARE_BY_ME} = CASE WHEN ${PostColumns.COLUMN_SHARE_BY_ME} THEN 0 ELSE 1 END
                WHERE id=?
            """.trimIndent(), arrayOf(id)
        )
    }

}