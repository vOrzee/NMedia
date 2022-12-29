package ru.netology.nmedia.adapters

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.*

class PostViewHolder(
    private val binding: FragmentCardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun renderingPostStructure(post: Post) {
        with(binding) {
            title.text = post.author
            datePublished.text = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.ROOT)
                .format(Date(post.published.toLong() * 1000))
            content.text = post.content
            like.text = NumberTranslator.translateNumber(post.likes)
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
                like.isClickable = false //защита от повторного запроса
                like.text =
                    if (!like.isChecked) { //для того чтобы каждый раз не запрашивать новый список постов
                        NumberTranslator.translateNumber(like.text.toString().toInt() - 1)
                    } else {
                        NumberTranslator.translateNumber(like.text.toString().toInt() + 1)
                    }
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                share.text =
                    if (!share.isChecked) { //для того чтобы каждый раз не запрашивать новый список постов
                        NumberTranslator.translateNumber(share.text.toString().toInt() - 1)
                    } else {
                        NumberTranslator.translateNumber(share.text.toString().toInt() + 1)
                    }
                onInteractionListener.onShare(post)
            }
            cardContent.setOnClickListener {
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