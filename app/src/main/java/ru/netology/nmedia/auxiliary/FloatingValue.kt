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
    fun showRegistrationDialog(context:Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.action_not_allowed)
            .setPositiveButton(R.string.sign_up) { _, _ ->
                Bundle().apply {
                    longArg = R.string.sign_up.toLong()
                }
            }
            .setNeutralButton(R.string.sign_in) { _,_ ->
                Bundle().apply {
                    longArg = R.string.sign_in.toLong()
                }
            }
            .setNegativeButton(R.string.no) { _,_ ->

            }
            .setCancelable(true)
            .create()
            .show()
    }
}