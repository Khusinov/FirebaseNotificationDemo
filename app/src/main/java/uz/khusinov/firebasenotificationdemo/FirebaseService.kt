package uz.khusinov.firebasenotificationdemo

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random
import androidx.lifecycle.LiveData

private const val CHANNEL_ID = "channelId"
private const val CHANNEL_NAME = "channelName"

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(newToken: String) {
        //SharedPref.instance.setPushToken(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        var intent = Intent(this, MainActivity::class.java)

        Log.d("AAAAA", message.data.toString())
        val title: String
        val body: String

        title = "test"
        body = "test"
        // notificationni dasturda tanlangan tilda chiqarish uchun buning uchun backenddan barcha tillarda yuborish kerak
//        when (SharedPref.instance.getLanguage()) {
//            CONSTANTS.UZ -> {
//                title = message.data[CONSTANTS.TITLE_UZ] ?: "Title"
//                body = message.data[CONSTANTS.MESSAGE_UZ] ?: "Body"
//            }
//            CONSTANTS.RU -> {
//                title = message.data[CONSTANTS.TITLE_RU] ?: "Title"
//                body = message.data[CONSTANTS.MESSAGE_RU] ?: "Body"
//            }
//            CONSTANTS.EN -> {
//                title = message.data[CONSTANTS.TITLE_EN] ?: "Title"
//                body = message.data[CONSTANTS.MESSAGE_EN] ?: "Body"
//            }
//            else -> {
//                title = message.data["title"] ?: "Title"
//                body = message.data["body"] ?: "Body"
//            }
//        }

        when (message.data["type"]) {
            "order_is_done" -> {
                intent.putExtra("order", "done")
            }
            else -> {
                intent.putExtra("page", "news")
                intent.putExtra("id", message.data["id"]?.toInt() ?: 0)
            }
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel(manager)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

// create big style notification if we need
//        val bigStyle = NotificationCompat.BigTextStyle()
//        bigStyle.bigText(body).setBigContentTitle(title)

        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultSoundUri)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(notificationID, notification)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(manager: NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
            description = "fcm"
            lightColor = Color.RED
            enableLights(true)
        }
        manager.createNotificationChannel(channel)
    }
}