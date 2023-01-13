package ru.netology.nmedia.auxiliary

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auxiliary.Companion.Companion.longArg
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg

object FloatingValue {
    var textNewPost = ""
    var currentFragment = ""
    fun renameUrl(nameResource: String, path: String, baseUrl: String = BuildConfig.BASE_URL): String {
        return "$baseUrl/$path/$nameResource"
    }

    fun agoToText(
        timeInSecond: Int,
        inLongAgoTextDescription:String,
        inRecentlyTextDescription:String = inLongAgoTextDescription,
        inYesterdayTextDescription:String = inRecentlyTextDescription,
        inTodayTextDescription:String = inYesterdayTextDescription,
        inHourTextDescription:String = inTodayTextDescription,
        inMinuteTextDescription:String = inHourTextDescription,
    ) = when (timeInSecond) {
        in 0..60 -> inMinuteTextDescription
        in 61..3600 -> inHourTextDescription
        in 3601..86400 -> inTodayTextDescription
        in 86401..172800 -> inYesterdayTextDescription
        in 172801..259200 -> inRecentlyTextDescription
        else -> inLongAgoTextDescription
    }

    private fun minuteOrHourToText(timeInSecond: Int, thisMinute: Boolean): String {
        val divisor = if (thisMinute) 60 else 3600
        if (timeInSecond / divisor in 5..20) return if (thisMinute) "минут" else "часов"
        return when ((timeInSecond / divisor) % 10) {
            1 -> if (thisMinute) "минуту" else "час"
            in 2..4 -> if (thisMinute) "минуты" else "часа"
            else -> "минут" //в случаях с часами этот вариант невозможен
        }
    }
}