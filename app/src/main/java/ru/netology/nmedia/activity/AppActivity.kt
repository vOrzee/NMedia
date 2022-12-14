package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.auxiliary.FloatingValue.textNewPost

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onBackPressed() {
        if (currentFragment != "NewPostFragment") super.onBackPressed()
    }

    override fun onStop() {
        currentFragment = ""
        textNewPost = ""
        super.onStop()
    }
}