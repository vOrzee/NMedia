package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.OnInteractionListener
import ru.netology.nmedia.adapters.PostAdapter
import ru.netology.nmedia.auxiliary.AndroidUtils
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val interactionListener = object : OnInteractionListener {
        override fun onLike(post: Post) {
            viewModel.likeById(post.id)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, post.content)
                type = "text/plain"
            }
            startActivity(intent)
            val shareIntent =
                Intent.createChooser(intent,getString(R.string.titleMain))
            startActivity(shareIntent)
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

//        viewModel.edited.observe(this) { post ->
//            if (post.id == 0L) {
//                return@observe
//            }
//            with(binding.saveTextField) {
//                requestFocus()
//                setText(post.content)
//            }
//        }
        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }

        binding.fab.setOnClickListener {
            newPostLauncher.launch()
        }

//        binding.saveButton.setOnClickListener {
//            with(binding.saveTextField) {
//                if (text.isNullOrBlank()) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        context.getString(R.string.error_empty_content),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@setOnClickListener
//                }
//
//                viewModel.changeContent(text.toString())
//                viewModel.save()
//                setText("")
//                clearFocus()
//                binding.descriptionByBack.visibility = View.GONE
//                binding.backButton.visibility = View.GONE
//                AndroidUtils.hideKeyboard(this)
//            }
//        }
//        binding.saveTextField.setOnFocusChangeListener { _, _ ->
//            binding.descriptionByBack.visibility = View.VISIBLE
//            binding.backButton.visibility = View.VISIBLE
//        }
//        binding.backButton.setOnClickListener {
//            with(binding.saveTextField) {
//                setText("")
//                clearFocus()
//                AndroidUtils.hideKeyboard(this)
//            }
//            binding.descriptionByBack.visibility = View.GONE
//            binding.backButton.visibility = View.GONE
//            viewModel.edited.value?.copy(id = 0)
//            viewModel.save()
//        }
    }
}
