package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
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
import ru.netology.nmedia.dto.AttachmentType
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
) : PagingDataAdapter<Post,PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding =
            FragmentCardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.renderingPostStructure(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostViewHolder(
    private val binding: FragmentCardPostBinding,
    private val onInteractionListener: OnInteractionListener,
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