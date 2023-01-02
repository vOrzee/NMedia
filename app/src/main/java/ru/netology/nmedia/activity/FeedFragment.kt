package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.OnInteractionListener
import ru.netology.nmedia.adapters.PostAdapter
import ru.netology.nmedia.auxiliary.Companion.Companion.longArg
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.concurrent.thread


class FeedFragment : Fragment() {

    val viewModel: PostViewModel by activityViewModels()


    private val interactionListener = object : OnInteractionListener {

        override fun onLike(post: Post) {
            viewModel.likeById(post)
        }

        override fun onShare(post: Post) {
            viewModel.shareById(post.id)
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, post.content)
            }

            val shareIntent =
                Intent.createChooser(intent, getString(R.string.chooser_share_post))
            startActivity(shareIntent)
        }

        override fun onRemove(post: Post) {
            viewModel.removeById(post.id)
        }

        override fun onEdit(post: Post) {
            viewModel.edit(post)
            findNavController().navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = post.content
                })
        }

        override fun onPlayVideo(post: Post) {
            val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.attachment?.url))
            if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(playIntent)
            }
        }

        override fun onPreviewPost(post: Post) {
            findNavController().navigate(
                R.id.action_feedFragment_to_postFragment,
                Bundle().apply {
                    longArg = post.id
                })
        }
    }

    lateinit var binding: FragmentFeedBinding
    lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFeedBinding.inflate(layoutInflater)
        adapter = PostAdapter(interactionListener)

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it.posts)
            binding.emptyText.isVisible = it.empty
        }
        viewModel.dataState.observe(viewLifecycleOwner) {
            binding.progress.isVisible = it is FeedModelState.Loading
            binding.swipe.isRefreshing = it is FeedModelState.Refresh
            if (it is FeedModelState.Error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
                    .show()
            }
            if (it is FeedModelState.Idle) {
                Toast.makeText(context, R.string.on_success, Toast.LENGTH_SHORT).show()
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.swipe.setOnRefreshListener {
            viewModel.refreshPosts()
        }

        return binding.root
    }

    override fun onResume() {
        currentFragment = javaClass.simpleName
        super.onResume()
    }
}
