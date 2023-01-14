package ru.netology.nmedia.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.databinding.FragmentCardPostBinding
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.FloatingValue.renameUrl
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.TimingSeparatorBinding
import ru.netology.nmedia.dto.*
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


    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            payloads.forEach {
                if (holder is PostViewHolder) {
                    if (it is Payload) {
                        holder.bind(it)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.fragment_card_post
            is TimingSeparator -> R.layout.timing_separator
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
            R.layout.timing_separator -> {
                val binding =
                    TimingSeparatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TimingSeparatorViewHolder(binding)
            }
            else -> error("unknown view type: $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("unknown item type")
            is TimingSeparator -> (holder as? TimingSeparatorViewHolder)?.bind(item)
        }
    }
}

data class Payload(
    val likes:LikePlayLoad? = null,
    val content:String? = null,
    val attachment: Attachment? = null,
    val image:String? = null,
)

data class LikePlayLoad(
    val likes:Int,
    val likedByMe:Boolean,
)

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

    override fun getChangePayload(oldItem: FeedItem, newItem: FeedItem): Any? {
        return when {
            (oldItem is Post && newItem is Post) ->
                Payload(
                    likes = if (newItem.likes != oldItem.likes)
                                LikePlayLoad(newItem.likes, newItem.likedByMe)
                            else null,
                    content = newItem.content.takeIf { it != oldItem.content},
                    attachment = newItem.attachment.takeIf { it != oldItem.attachment},
                )
            (oldItem is Ad && newItem is Ad) ->
                Payload(
                    image = newItem.image.takeIf { it != oldItem.image},
                )
            else -> null
        }
    }

}

class TimingSeparatorViewHolder(
    private val binding: TimingSeparatorBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(timingSeparator: TimingSeparator) {
        binding.howOlder.text = timingSeparator.text
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

    @SuppressLint("SetTextI18n")
    fun bind(payload: Payload) {
        payload.likes?.also { liked ->
            if (!liked.likedByMe) {
                binding.like.text = (payload.likes.likes).toString()
                ObjectAnimator.ofFloat(
                    binding.like,
                    View.ROTATION,
                    0F, 360F
                ).start()
            } else {
                binding.like.text = (payload.likes.likes).toString()
                ObjectAnimator.ofPropertyValuesHolder(
                    binding.like,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F, 1.0F, 1.2F),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F, 1.0F, 1.2F)
                ).start()
            }
            binding.like.isChecked = liked.likedByMe
        }
        payload.content?.let(binding.content::setText)
    }

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