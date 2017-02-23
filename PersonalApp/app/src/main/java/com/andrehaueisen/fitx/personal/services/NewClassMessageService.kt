package com.andrehaueisen.fitx.personal.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v7.app.NotificationCompat
import android.util.Log
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.personal.PersonalActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by andre on 1/25/2017.
 */
class NewClassMessageService : FirebaseMessagingService() {

    private val NOTIFICATION_ID = 1

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        Log.i("NewClassMessageService", remoteMessage?.from)
        Log.i("NewClassMessageService", remoteMessage?.messageId)
        Log.i("NewClassMessageService", remoteMessage?.sentTime.toString())

        if (remoteMessage != null) {
            if (remoteMessage.collapseKey.equals("New PersonalClass")) {
                val messageData = remoteMessage.data
                val title = messageData.get("title")
                val body = messageData.get("message")

                if (title != null && body != null)
                    createNotification(title, body)
            }
        }
    }

    fun createNotification(title: String, body: String) {

        val context = application.applicationContext
        val appIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)

        val builder = NotificationCompat.Builder(applicationContext)
                .setLargeIcon(appIcon)
                .setColor(context.resources.getColor(R.color.colorPrimaryDark))
                .setSmallIcon(R.drawable.ic_new_class_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, PersonalActivity::class.java), 0)
        builder.setContentIntent(pendingIntent)

        val notificationManager = getSystemService (NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())

        /*connect.data("registration_id", databaseToken)
        connect.data("collapse_key", "New PersonalClass")
        connect.data("data.title", "Congratulation! New Class!")
        connect.data("data.message", message)*/
    }
}