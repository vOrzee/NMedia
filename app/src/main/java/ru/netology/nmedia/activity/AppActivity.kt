package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.Companion.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity(R.layout.activity_app)
//{
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivityAppBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        intent?.let {
//            if (it.action != Intent.ACTION_SEND) {
//                return@let
//            }
//            val text = it.getStringExtra(Intent.EXTRA_TEXT)
//            if (text.isNullOrBlank()) {
//                Snackbar.make(
//                    binding.root,
//                    R.string.error_empty_content,
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .setAction(android.R.string.ok) {
//                        finish()
//                    }
//                    .show()
//            }
//            findNavController(R.id.navigation_fragment).navigate(R.id.action_feedFragment_to_newPostFragment,
//                Bundle().apply {
//                    textArg = text
//                })
//        }
//    }
//}