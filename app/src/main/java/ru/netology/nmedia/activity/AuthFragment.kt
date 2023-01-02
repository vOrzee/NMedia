package ru.netology.nmedia.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

class AuthFragment : Fragment() {
    private val binding by lazy { FragmentAuthBinding.inflate(layoutInflater) }
    private val viewModel: AuthViewModel by activityViewModels()

    private var fragmentBinding: FragmentAuthBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentBinding = binding


        binding.authBlock.isVisible = true

        val isSignIn = arguments?.textArg == getString(R.string.sign_in)

        if (isSignIn) {
            binding.signUpGroup.visibility = View.GONE
        } else {
            binding.signUpGroup.visibility = View.VISIBLE
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }
        binding.uploadAvatar.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null /*&& viewModel.getEditedPostImgRes().isNullOrBlank()*/) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        with(binding) {

            removePhoto.setOnClickListener {
                //viewModel.deleteAttachment()
                viewModel.changePhoto(null, null)
            }
            enterInSystem.setOnClickListener {
                if (isSignIn) {
                    inputEditPasswordConfirm.text = inputEditPassword.text
                    Toast.makeText(requireContext(),inputEditPassword.text,Toast.LENGTH_SHORT).show()
                } else {
                    if (inputEditPasswordConfirm.text.toString() != inputEditPassword.text.toString()) {
                        Toast.makeText(requireContext(),
                            R.string.error_password_not_ident,
                            Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            return root
        }

    }
}