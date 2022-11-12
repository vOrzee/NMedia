package ru.netology.nmedia.holders

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.OnInteractionListener
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
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
            likes.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setImageResource(
                if (post.sharedByMe) R.drawable.ic_shared_24 else R.drawable.ic_baseline_share_24
            )
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            moreVert.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}