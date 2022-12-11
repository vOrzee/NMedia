package ru.netology.nmedia.activity

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import ru.netology.nmedia.R

class NewPostActivity() : AppCompatActivity() {
    private val binding by lazy { ActivityNewPostBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        setContentView(binding.root)

        with(binding.edit) {
            val text = intent.getStringExtra(POST_TEXT)
            setText(text)
            requestFocus()
        }

        binding.fabComplete.setOnClickListener{
            val intent = Intent()

            if (binding.edit.text.isNullOrBlank()) {
                Toast.makeText(
                    this,
                    this.getString(R.string.error_empty_content),
                    Toast.LENGTH_SHORT)
                    .show()
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.edit.text.toString()
                intent.putExtra(POST_TEXT, content)
                setResult(Activity.RESULT_OK, intent)
            }

            finish()
        }
        binding.fabCancel.setOnClickListener{
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        intent.putExtra(POST_TEXT, binding.edit.text.toString())
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}