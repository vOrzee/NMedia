package ru.netology.nmedia.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "myChannel"
    private val gson = Gson()

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let { actionType ->
            val listAction = Action.values().map { it.name }
            if (!listAction.contains(actionType)) {
                handleUnknownAction()
                return
            }
            when (Action.valueOf(actionType)) {
                Action.LIKE -> handleLike(gson.fromJson(message.data[content], Like::class.java))
                Action.SEND_POST -> handleSendPost(
                    gson.fromJson(
                        message.data[content],
                        NewPost::class.java
                    )
                )
            }
            return
        }
        val  testInputPushValue = message.data.values.map {
            gson.fromJson(it, Test::class.java)
        }[0]
        val myId = appAuth.authStateFlow.value.id
        when {
            testInputPushValue.recipientId == myId -> {
                handleTestAction(testInputPushValue,"Персональная рассылка")
            }
            testInputPushValue.recipientId == null -> {
                handleTestAction(testInputPushValue,"Массовая рассылка")
            }
            testInputPushValue.recipientId == 0L  -> {
                println("сервер считает, что у нас анонимная аутентификация, переотправляем токен")
                appAuth.sendPushToken()
            }
            testInputPushValue.recipientId != 0L -> {
                println("сервер считает, что у на нашем устройстве другая аутентификация, переотправляем токен")
                appAuth.sendPushToken()
            }
        }
    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }

    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.netology_foreground)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        getString(
                            R.string.notification_user_liked,
                            content.userName,
                            content.postTitle,
                        )
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleSendPost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.netology_foreground)
            .setContentTitle(
                getString(
                    R.string.notification_user_sending_post,
                    content.userName
                )
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content.textPost)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleUnknownAction() {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.netology_foreground)
            .setContentTitle(getString(R.string.unknown_action))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.obscure_operation))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleTestAction(content: Test, message: String) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.netology_foreground)
            .setContentTitle(content.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    enum class Action {
        LIKE,
        SEND_POST
    }

    data class Like(
        val userId: Long,
        val userName: String,
        val postId: Long,
        val postTitle: String,
    )

    data class NewPost(
        val userName: String,
        val textPost: String
    )

    data class Test(
        val recipientId: Long? = null,
        val content: String
    )
}
