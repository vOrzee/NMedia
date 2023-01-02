package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.SimpleAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import ru.netology.nmedia.R
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.adapters.PostAdapter
import ru.netology.nmedia.auxiliary.Companion.Companion.longArg
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.FloatingValue
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.auxiliary.NumberTranslator.translateNumber
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.*

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater)
        val viewModel: PostViewModel by activityViewModels()

        with(binding.singlePost) {
            viewModel.data.observe(viewLifecycleOwner) { state ->
                val posts = state.posts
                val post = posts.find { it.id == arguments?.longArg }
                if (post != null) {
                    title.text = post.author
                    datePublished.text = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.ROOT)
                        .format(Date(post.published.toLong() * 1000))
                    content.text = post.content
                    like.text = translateNumber(post.likes)
                    like.isChecked = post.likedByMe
                    share.text = translateNumber(post.countShared)
                    share.isChecked = post.sharedByMe
                    view.text = translateNumber(post.countViews)
                    view.isChecked = post.viewedByMe
                    moreVert.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
                    Glide.with(avatar)
                        .load(FloatingValue.renameUrl(post.authorAvatar ?: "", "avatars"))
                        .placeholder(R.drawable.ic_image_not_supported_24)
                        .error(R.drawable.ic_not_avatars_24)
                        .circleCrop()
                        .timeout(10_000)
                        .into(avatar)
                    if (post.attachment != null) {
                        attachmentContent.visibility = View.VISIBLE
                        Glide.with(imageAttachment)
                            .load(FloatingValue.renameUrl(post.attachment.url, "media"))
                            .placeholder(R.drawable.not_image_1000)
                            .timeout(10_000)
                            .into(imageAttachment)
                        descriptionAttachment.text = post.attachment.description
                        playButtonVideoPost.isVisible = (post.attachment.type == AttachmentType.VIDEO)
                    } else {
                        attachmentContent.visibility = View.GONE
                    }

                    like.setOnClickListener {
                        like.isChecked =
                            !like.isChecked //отменяем смену состояния чтобы получить его с сервера
                        like.isClickable = false //защита от повторного запроса
                        viewModel.likeById(post)
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
                            PopupMenu(binding.root.context, binding.singlePost.moreVert)
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
                        val playIntent = Intent(Intent.ACTION_VIEW, Uri.parse(post.attachment?.url))
                        if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                            startActivity(playIntent)
                        }
                    }
                }
            }
        }
        with(binding.listComment) {
            viewModel.data.observe(viewLifecycleOwner) { state ->
                state.posts.find { it.id == arguments?.longArg }
                    ?.let { viewModel.getCommentsById(it) }
                //TODO комментарии считываются, сохраняются, осталось добавить отображение
            }
        }
        return binding.root
    }

    override fun onStart() {
        currentFragment = javaClass.simpleName
        super.onStart()
    }
}
