package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.Companion.Companion.longArg
import ru.netology.nmedia.activity.Companion.Companion.textArg
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.auxiliary.NumberTranslator.translateNumber
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentPostBinding.inflate(layoutInflater)
        val viewModel: PostViewModel by viewModels(::requireParentFragment)

        with(binding.scrollContent) {
            viewModel.data.observe(viewLifecycleOwner) { posts ->
                val post = posts.find { it.id == arguments?.longArg }
                if (post != null) {
                    title.text = post.title
                    datePublished.text = post.published
                    content.text = post.content
                    like.text = translateNumber(post.countLikes)
                    like.isChecked = post.likedByMe
                    share.text = translateNumber(post.countShared)
                    share.isChecked = post.sharedByMe
                    view.text = translateNumber(post.countViews)
                    view.isChecked = post.viewedByMe
                    if (!post.videoUrl.isNullOrBlank()) {
                        videoContent.visibility = View.VISIBLE
                    } else {
                        videoContent.visibility = View.GONE
                    }

                    like.setOnClickListener {
                        viewModel.likeById(post.id)
                    }

                    share.setOnClickListener {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, post.content)
                        }

                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                    }

                    moreVert.setOnClickListener {
                        val popupMenu =
                            PopupMenu(binding.root.context, binding.scrollContent.moreVert)
                        popupMenu.apply {
                            inflate(R.menu.options_post)
                            setOnMenuItemClickListener {
                                when (it.itemId) {
                                    R.id.remove -> {
                                        findNavController().navigateUp()
                                        viewModel.removeById(post.id)
                                        true
                                    }
                                    R.id.edit -> {
                                        viewModel.edit(post)
                                        findNavController().navigate(R.id.action_postFragment_to_newPostFragment,
                                            Bundle().apply {
                                                textArg = post.content
                                            })
                                        true
                                    }
                                    else -> false
                                }
                            }
                        }.show()
                        popupMenu.setOnDismissListener {
                            moreVert.isChecked = false
                        }
                    }

                    playButtonVideoPost.setOnClickListener {
                        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                        if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(playIntent)
                        }
                    }
                }
            }
        }
        return binding.root
    }
}
