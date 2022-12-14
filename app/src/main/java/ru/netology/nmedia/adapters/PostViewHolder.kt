package ru.netology.nmedia.adapters

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: FragmentCardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun renderingPostStructure(post: Post) {
        with(binding) {
            title.text = post.title
            datePublished.text = post.published
            content.text = post.content
            like.text = NumberTranslator.translateNumber(post.countLikes)
            like.isChecked = post.likedByMe
            share.text = NumberTranslator.translateNumber(post.countShared)
            share.isChecked = post.sharedByMe
            view.text = NumberTranslator.translateNumber(post.countViews)
            view.isChecked = post.viewedByMe
            if (!post.videoUrl.isNullOrBlank()) {
                videoContent.visibility = View.VISIBLE
            } else {
                videoContent.visibility = View.GONE
            }
            postListeners(post)
        }
    }

    private fun postListeners(post: Post) {
        with(binding) {
            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            postCard.setOnClickListener {
                onInteractionListener.onPreviewPost(post)
            }
            playButtonVideoPost.setOnClickListener {
                onInteractionListener.onPlayVideo(post)
            }
            moreVert.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.apply {
                    inflate(ru.netology.nmedia.R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            ru.netology.nmedia.R.id.remove -> {
                                moreVert.isChecked = false
                                onInteractionListener.onRemove(post)
                                true
                            }
                            ru.netology.nmedia.R.id.edit -> {
                                moreVert.isChecked = false
                                onInteractionListener.onEdit(post)
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
        }
    }
}