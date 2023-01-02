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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auxiliary.Companion.Companion.textArg
import ru.netology.nmedia.auxiliary.ConstantValues.noPhoto
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.viewmodel.AuthViewModel
import kotlin.coroutines.EmptyCoroutineContext

class AuthFragment : Fragment() {
    private val binding by lazy { FragmentAuthBinding.inflate(layoutInflater) }
    private val viewModel: AuthViewModel by viewModels()

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
            binding.enterInSystem.setText(R.string.sign_in)
        } else {
            binding.signUpGroup.visibility = View.VISIBLE
            binding.enterInSystem.setText(R.string.sign_up)
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
                .cropSquare()
                .compress(512)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.uploadAvatar.visibility = View.VISIBLE
                binding.photoContainer.visibility = View.GONE
                return@observe
            }
            binding.uploadAvatar.visibility = View.GONE
            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        viewModel.dataState.observe(viewLifecycleOwner){
            when(it) {
                0 -> findNavController().navigateUp()
                -1 -> {
                    binding.errorMessage.visibility = View.GONE
                }
                1 -> {
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.setText(R.string.not_pass_enter)
                }
                else -> {
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = getString(R.string.eror_code) + ": $it"
                }
            }
        }

        with(binding) {

            removePhoto.setOnClickListener {
                //viewModel.deleteAttachment()
                viewModel.changePhoto(null, null)
            }
            enterInSystem.setOnClickListener {
                binding.errorMessage.visibility = View.GONE
                if (isSignIn) {
                    inputEditPasswordConfirm.text = inputEditPassword.text
                    val scope = CoroutineScope(EmptyCoroutineContext)
                    val login = binding.inputEditLogin.text.toString()
                    val pass = binding.inputEditPassword.text.toString()
                    scope.launch {
                        viewModel.login(login,pass)
                    }

                } else {
                    if (inputEditPasswordConfirm.text.toString() != inputEditPassword.text.toString()) {
                        Toast.makeText(requireContext(),
                            R.string.error_password_not_ident,
                            Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        val scope = CoroutineScope(EmptyCoroutineContext)
                        val login = binding.inputEditLogin.text.toString()
                        val pass = binding.inputEditPassword.text.toString()
                        val name = binding.inputEditName.text.toString()
                        val photo = viewModel.photo.value?.file?.let { file -> MediaUpload(file) }
                        scope.launch {
                            viewModel.registerWithPhoto(login,pass,name,photo)
                        }
                    }
                }
            }
            return root
        }

    }
}