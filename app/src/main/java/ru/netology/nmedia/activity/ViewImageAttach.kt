package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.FloatingValue
import ru.netology.nmedia.databinding.FragmentAttachmentImageViewBinding
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class ViewImageAttach : Fragment() {

    val viewModel: PostViewModel by activityViewModels()

    lateinit var binding: FragmentAttachmentImageViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAttachmentImageViewBinding.inflate(layoutInflater)

        val url = arguments?.textArg ?: ""

        Glide.with(binding.image)
            .load(FloatingValue.renameUrl(url, "media"))
            .placeholder(R.drawable.not_image_1000)
            .timeout(10_000)
            .into(binding.image)



        binding.fabCancel.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }
}