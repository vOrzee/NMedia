package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribe()
        setListeners()
    }

    private fun subscribe() {
        viewModel.data.observe(this) { post ->
            with(binding) {
                title.text = post.title
                datePublished.text = post.published
                content.text = post.content
                countLikes.text = NumberTranslator.translateNumber(post.countLikes)
                countShare.text = NumberTranslator.translateNumber(post.countShared)
                countViewed.text = NumberTranslator.translateNumber(post.countViews)
                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_sharp_favorite_24
                )
                share.setImageResource(
                    if (post.sharedByMe) R.drawable.ic_shared_24 else R.drawable.ic_baseline_share_24
                )
            }
        }
    }

    private fun setListeners() {
        binding.likes.setOnClickListener {
            viewModel.like()
        }
        binding.share.setOnClickListener {
            viewModel.share()
        }
    }
}