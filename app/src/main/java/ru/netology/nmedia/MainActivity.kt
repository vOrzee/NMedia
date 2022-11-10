package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            title = "Нетология. Университет интернет-профессий будущего",
            content = "Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "21 мая в 18:36"
        )

        with(binding) {
            title.text = post.title
            datePublished.text = post.published
            content.text = post.content
            countLikes.text = NumberTranslator.translateNumber(post.countLikes)
            countShare.text = NumberTranslator.translateNumber(post.countShared)
            countViewed.text = NumberTranslator.translateNumber(post.countViews)

            if (post.likedByMe) {
                likes?.setImageResource(R.drawable.ic_liked_24)
            }
            if (post.sharedByMe) {
                likes?.setImageResource(R.drawable.ic_shared_24)
            }

            likes?.setOnClickListener {
                if(post.likedByMe) {
                    post.countLikes--
                } else {
                    post.countLikes++
                }
                countLikes.text = NumberTranslator.translateNumber(post.countLikes)
                post.likedByMe = !post.likedByMe
                likes.setImageResource(
                    if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_sharp_favorite_24
                )
            }
            share?.setOnClickListener {
                if(post.sharedByMe) {
                    post.countShared--
                } else {
                    post.countShared++
                }
                countShare.text = NumberTranslator.translateNumber(post.countShared)
                post.sharedByMe = !post.sharedByMe
                share.setImageResource(
                    if (post.sharedByMe) R.drawable.ic_shared_24 else R.drawable.ic_baseline_share_24
                )
            }
        }
    }
}