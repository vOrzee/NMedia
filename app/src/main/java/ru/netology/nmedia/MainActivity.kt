package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.OnInteractionListener
import ru.netology.nmedia.adapters.PostAdapter
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }
        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
        }
        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }
        override fun onEdit(post: Post) {
            viewModel.edit(post)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val adapter = PostAdapter(interactionListener)
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }
}
