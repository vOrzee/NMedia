package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.Companion.Companion.longArg
import ru.netology.nmedia.activity.Companion.Companion.textArg
import ru.netology.nmedia.adapters.OnInteractionListener
import ru.netology.nmedia.adapters.PostAdapter
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentFragment = javaClass.simpleName

        val binding = FragmentFeedBinding.inflate(layoutInflater)

        val viewModel: PostViewModel by viewModels(::requireParentFragment)

        val adapter = PostAdapter(
            object : OnInteractionListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
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
                    fillDefaultPostsIfThereAreNoPosts(viewModel)
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
                    val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
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
        )

        binding.list.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }
        return binding.root
    }

    private fun fillDefaultPostsIfThereAreNoPosts(viewModel: PostViewModel) {
        //Хотел через activity?.recreate() - оказалось плохая идея
        if (viewModel.data.value.isNullOrEmpty()) {
            val intent = Intent().apply {
                action = "KEY_INTENT:734-68786151567"
                type = "text/plain"
                putExtra("noTypedRefSimpleModIntent", "nothing")
            }
            startActivity(intent)
        }
    }

}
