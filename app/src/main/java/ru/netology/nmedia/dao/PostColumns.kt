package ru.netology.nmedia.dao

object PostColumns {
    const val TABLE_NAME = "posts"
    const val COLUMN_ID = "id"
    const val COLUMN_AUTHOR_ID = "authorId"
    const val COLUMN_TITLE = "author"
    const val COLUMN_CONTENT = "content"
    const val COLUMN_PUBLISHED = "published"
    const val COLUMN_LIKED_BY_ME = "likedByMe"
    const val COLUMN_LIKES = "likes"
    const val COLUMN_SHARE_BY_ME = "sharedByMe"
    const val COLUMN_SHARES = "shares"
    const val COLUMN_VIEW_BY_ME = "viewByMe"
    const val COLUMN_VIEWS = "view"
    const val COLUMN_VIDEO_URL = "videoUrl"
    val ALL_COLUMNS = arrayOf(
        COLUMN_ID,
        COLUMN_AUTHOR_ID,
        COLUMN_TITLE,
        COLUMN_PUBLISHED,
        COLUMN_CONTENT,
        COLUMN_LIKED_BY_ME,
        COLUMN_LIKES,
        COLUMN_SHARE_BY_ME,
        COLUMN_SHARES,
        COLUMN_VIEW_BY_ME,
        COLUMN_VIEWS,
        COLUMN_VIDEO_URL
    )

    val DDL = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_AUTHOR_ID INTEGER NOT NULL,
            $COLUMN_TITLE TEXT NOT NULL DEFAULT "Какой-то заголовок",
            $COLUMN_PUBLISHED TEXT NOT NULL DEFAULT "Только что",
            $COLUMN_CONTENT TEXT NOT NULL,
            $COLUMN_LIKED_BY_ME BOOLEAN NOT NULL DEFAULT false,
            $COLUMN_LIKES INTEGER NOT NULL DEFAULT 0,
	        $COLUMN_SHARE_BY_ME BOOLEAN NOT NULL DEFAULT false,
	        $COLUMN_SHARES INTEGER NOT NULL DEFAULT 0,	
	        $COLUMN_VIEW_BY_ME BOOLEAN NOT NULL DEFAULT false,
	        $COLUMN_VIEWS INTEGER NOT NULL DEFAULT 0,		
            $COLUMN_VIDEO_URL TEXT
        );
        """.trimIndent()

    val DEFAULT_POSTS = """                      
        INSERT INTO $TABLE_NAME ($COLUMN_AUTHOR_ID, $COLUMN_TITLE, $COLUMN_PUBLISHED, $COLUMN_CONTENT, $COLUMN_LIKES, $COLUMN_SHARES, $COLUMN_VIEWS, $COLUMN_VIDEO_URL) VALUES (
            "1",
            "Нетология. Университет интернет-профессий будущего",
            "21 мая в 18:36",
            "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            9999,
            999,
            99999,
            null
         ),(
            "1",
            "Нетология. Университет интернет-профессий будущего",
            "18 сентября в 10:12",
            "Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            9999,
            999,
            99999,
            null
         ),(
            "1",
            "Нетология. Университет интернет-профессий будущего",
            "19 сентября в 12:12",
            "Третий пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            9999,
            999,
            99999,
            null
         ),(
            "1",
            "Нетология. Университет интернет-профессий будущего",
            "20 сентября в 12:12",
            "Четвёртый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            9999,
            999,
            99999,
            null
         ),(
            "1",
            "Нетология. Университет интернет-профессий будущего",
            "21 сентября в 12:12",
            "Пятый пост. Знаний хватит на всех, на следующей неделе разбираемся с.. Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            9999,
            999,
            99999,
            "https://www.youtube.com/watch?v=ir0vFRaQ-Hw"
         );
    """.trimIndent()

}