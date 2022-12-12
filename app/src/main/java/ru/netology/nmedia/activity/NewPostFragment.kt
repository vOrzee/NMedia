package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.activity.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.AndroidUtils.showKeyboardFrom
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment() : Fragment() {

    private val binding by lazy { FragmentNewPostBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: PostViewModel by viewModels(::requireParentFragment)
        with(binding) {
            arguments?.textArg?.let {
                edit.setText(it)
            }
            edit.requestFocus()




                fabComplete.setOnClickListener {
                    if (!edit.text.isNullOrBlank()) {
                        val content = edit.text.toString()
                        viewModel.changeContent(content)
                        viewModel.save()
                    }
                    findNavController().navigateUp()
                }

                fabCancel.setOnClickListener {
                    viewModel.save()
                    findNavController().navigateUp()
                }
                return root
            }
        }
    }