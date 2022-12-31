package ru.netology.nmedia.auxiliary

import ru.netology.nmedia.BuildConfig

object FloatingValue {
    var textNewPost = ""
    var currentFragment = ""
    fun renameUrl(nameResource: String, path: String, baseUrl: String = BuildConfig.BASE_URL): String {
        return "$baseUrl/$path/$nameResource"
    }
}