package ru.netology.nmedia.activity

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.AndroidUtils.hideKeyboard
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.auxiliary.FloatingValue.textNewPost

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    override fun onBackPressed() {
        if (currentFragment == "NewPostFragment") {
            findViewById<FloatingActionButton>(R.id.fab_cancel).callOnClick()
            return
        }
        super.onBackPressed()
    }

    override fun onStop() {
        currentFragment = ""
        textNewPost = ""
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}