package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import ru.netology.nmedia.dto.Post
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.FloatingValue.renameUrl
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.view.load
import java.text.SimpleDateFormat
import java.util.*

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onPlayVideo(post: Post) {}
    fun onPreviewPost(post: Post) {}
    fun onPreviewAttachment(post: Post) {}
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : PagingDataAdapter<FeedItem,RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.fragment_card_post
            null -> error("unknown view type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.fragment_card_post -> {
                val binding =
                    FragmentCardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            else -> error("unknown view type: $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ad: Ad) {
        binding.image.load(renameUrl(ad.image, "media"))
    }
}

class PostViewHolder(
    private val binding: FragmentCardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
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
            Glide.with(avatar)
                .load(renameUrl(post.authorAvatar ?: "", "avatars"))
                .placeholder(R.drawable.ic_image_not_supported_24)
                .error(R.drawable.ic_not_avatars_24)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)
            moreVert.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            if (post.attachment != null) {
                attachmentContent.isVisible = true
                Glide.with(imageAttachment)
                    .load(renameUrl(post.attachment.url, "media"))
                    .placeholder(R.drawable.not_image_1000)
                    .timeout(10_000)
                    .into(imageAttachment)
                descriptionAttachment.text = post.attachment.description
                playButtonVideoPost.isVisible = (post.attachment.type == AttachmentType.VIDEO)
            } else {
                attachmentContent.visibility = View.GONE
            }
            postListeners(post)
        }
    }

    private fun postListeners(post: Post) {
        with(binding) {
            like.setOnClickListener {
                //like.isClickable = false //защита от повторного запроса
                like.isChecked = !like.isChecked //Инвертируем нажатие
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

            imageAttachment.setOnClickListener {
                onInteractionListener.onPreviewAttachment(post)
            }
            moreVert.setOnClickListener {
                val popupMenu = PopupMenu(it.context, it)
                popupMenu.apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                moreVert.isChecked = false
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
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