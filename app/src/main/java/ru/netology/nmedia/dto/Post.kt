package ru.netology.nmedia.dto

import android.util.JsonWriter
import android.webkit.JsPromptResult
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import org.json.JSONArray
import org.json.JSONObject

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 9_999,
    val sharedByMe: Boolean = false,
    val countShared: Int = 999,
    val viewedByMe: Boolean = false,
    val countViews: Int = 99_999,
    val videoUrl: String? = null,
    //val attachment: String? = null,

)

data class Attachment(
    val url: String,
    val description: String?,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}