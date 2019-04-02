package vdream.vd.com.vdream.utils

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.app.NotificationManager
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationCompat
import vdream.vd.com.vdream.R
import vdream.vd.com.vdream.view.activity.SplashActivity


class PushMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(msg: RemoteMessage?) {
        if(msg != null)
            createNotification(msg!!.data)
    }

    private fun createNotification(dataMap: Map<String, String>) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val nBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.appicon)
                .setContentTitle(dataMap["title"])
                .setContentText(dataMap["msg"])
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(1000, 1000))
                .setLights(Color.WHITE, 1500, 1500)
                .setContentIntent(contentIntent)

        val nManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.notify(0 /* ID of notification */, nBuilder.build())
    }
}