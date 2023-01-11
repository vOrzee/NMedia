package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import ru.netology.nmedia.R
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import androidx.paging.map
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.adapters.CommentAdapter
import ru.netology.nmedia.adapters.OnInteractionListenerComment
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auxiliary.Companion.Companion.longArg
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.FloatingValue
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.auxiliary.NumberTranslator.translateNumber
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Comment
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PostFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostBinding.inflate(layoutInflater)
        val viewModel: PostViewModel by activityViewModels()

        val authViewModel: AuthViewModel by viewModels()

        var menuProvider: MenuProvider? = null

        val adapter = CommentAdapter(object : OnInteractionListenerComment {
            override fun onLike(comment: Comment) {
                if (authViewModel.authenticated) {
                    //TODO пока недоступно
                    //viewModel.likeByIdComment(comment)
                } else {
                    AlertDialog.Builder(context)
                        .setMessage(R.string.action_not_allowed)
                        .setPositiveButton(R.string.sign_up) { _, _ ->
                            findNavController().navigate(
                                R.id.action_postFragment_to_authFragment,
                                Bundle().apply {
                                    textArg = getString(R.string.sign_up)
                                }
                            )
                        }
                        .setNeutralButton(R.string.sign_in) { _, _ ->
                            findNavController().navigate(
                                R.id.action_postFragment_to_authFragment,
                                Bundle().apply {
                                    textArg = getString(R.string.sign_in)
                                }
                            )
                        }
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .create()
                        .show()
                }
            }
        })

        authViewModel.data.observe(viewLifecycleOwner) {
            menuProvider?.let(requireActivity()::removeMenuProvider)
            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)

                    menu.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
                    menu.setGroupVisible(R.id.authenticated, authViewModel.authenticated)

                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.signin -> {
                            findNavController().navigate(
                                R.id.action_postFragment_to_authFragment,
                                Bundle().apply {
                                    textArg = getString(R.string.sign_in)
                                }
                            )
                            true
                        }
                        R.id.signup -> {
                            findNavController().navigate(
                                R.id.action_postFragment_to_authFragment,
                                Bundle().apply {
                                    textArg = getString(R.string.sign_up)
                                }
                            )
                            true
                        }
                        R.id.signout -> {
                            AlertDialog.Builder(requireActivity())
                                .setTitle(R.string.are_you_suare)
                                .setPositiveButton(R.string.yes) { _, _ ->
                                    appAuth.removeAuth()
                                }
                                .setCancelable(true)
                                .setNegativeButton(R.string.no, null)
                                .create()
                                .show()
                            true
                        }
                        else -> false
                    }
                }
            }.apply {
                menuProvider = this
            }, viewLifecycleOwner)
        }
        binding.listComment.adapter = adapter
        with(binding.singlePost) {
            lifecycleScope.launchWhenCreated {
                viewModel.data.collectLatest { state ->
                    val posts = state.filter { it.id == arguments?.longArg }
                    posts.map { post ->
                        title.text = post.author
                        datePublished.text =
                            SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.ROOT)
                                .format(Date(post.published.toLong() * 1000))
                        content.text = post.content
                        like.text = translateNumber(post.likes)
                        like.isChecked = post.likedByMe
                        share.text = translateNumber(post.countShared)
                        share.isChecked = post.sharedByMe
                        view.text = translateNumber(post.countViews)
                        view.isChecked = post.viewedByMe
                        moreVert.visibility =
                            if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
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
                            playButtonVideoPost.isVisible =
                                (post.attachment.type == AttachmentType.VIDEO)
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
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.chooser_share_post)
                                )
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
                            val playIntent =
                                Intent(Intent.ACTION_VIEW, Uri.parse(post.attachment?.url))
                            if (playIntent.resolveActivity(requireContext().packageManager) != null) {
                                startActivity(playIntent)
                            }
                        }
                        viewModel.getCommentsById(post)
                        //TODO пока комментарии только отображаются
                    }
                }
            }
        }

        viewModel.dataComment.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        return binding.root
    }

    override fun onStart() {
        currentFragment = javaClass.simpleName
        super.onStart()
    }
}