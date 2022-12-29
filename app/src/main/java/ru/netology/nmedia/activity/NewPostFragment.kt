package ru.netology.nmedia.activity

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.auxiliary.AndroidUtils.hideKeyboard
import ru.netology.nmedia.auxiliary.AndroidUtils.showKeyboard
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.FloatingValue.currentFragment
import ru.netology.nmedia.auxiliary.FloatingValue.textNewPost
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {

    private val binding by lazy { FragmentNewPostBinding.inflate(layoutInflater) }
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        with(binding) {

            arguments?.textArg?.let {
                edit.setText(it)
            }

            if (edit.text.isNullOrBlank()) {
                edit.setText(textNewPost)
            }

            edit.requestFocus()


            clickListeners()

            return root
        }
    }

    override fun onStart() {
        currentFragment = javaClass.simpleName
        super.onStart()
    }

    private fun clickListeners() {
        with(binding) {

            fabComplete.setOnClickListener {
                if (!edit.text.isNullOrBlank()) {
                    val content = edit.text.toString()
                    viewModel.changeContent(content)
                    viewModel.save()
                }
                hideKeyboard(root)
                fabComplete.isVisible = false
                fabCancel.isVisible = false
                edit.isVisible = false
                savingProgressBar.isVisible = true
            }
            viewModel.postCreated.observe(viewLifecycleOwner) {
                viewModel.loadPosts()
                findNavController().navigateUp()
            }

            fabCancel.setOnClickListener {
                if (viewModel.getEditedId() == 0L) {
                    textNewPost = edit.text.toString()
                } else {
                    edit.text.clear()
                    viewModel.save()
                }
                hideKeyboard(root)
                findNavController().navigateUp()
            }

        }
    }
}