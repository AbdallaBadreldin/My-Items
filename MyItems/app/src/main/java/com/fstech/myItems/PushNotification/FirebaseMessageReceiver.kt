package store.msolapps.flamingo.PushNotification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.fstech.myItems.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import store.msolapps.flamingo.presentation.home.MainActivity


const val channelId = "notification_channel"
const val channelName = "com.momentum.flamingo.PushNotification"
private var notificationCounter = 0

private const val TAG = "FirebaseMR"

@SuppressLint("MissingFirebaseInstanceTokenRefresh")

class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("fcm_token", "onNewToken: $p0")
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        if (p0.notification != null) {
            Log.d(TAG, "onMessageReceived title: ${p0.notification!!.title}")
            Log.d(TAG, "onMessageReceived body: ${p0.notification!!.body}")
            Log.d(TAG, "onMessageReceived body: ${p0.notification!!.tag}")
            Log.d(TAG, "onMessageReceived body: ${p0.notification!!.channelId}")
            Log.d(TAG, "onMessageReceived body: ${p0.notification!!.notificationCount}")
            Log.d(TAG, "onMessageReceived body: ${p0.data.getValue("url")}")
            if (p0.data.getValue("url").toString()
                    .isNullOrEmpty()
            )
//                generateNotification(p0.notification!!.title!!, p0.notification!!.body!!)
            else {
//                generateNotificationWithUrl(
//                    p0.notification!!.title!!,
//                    p0.notification!!.body!!,
//                    p0.data.getValue("url").toString()
//                )
            }
        }
    }

 /*   private fun generateNotificationWithUrl(title: String, message: String, url: String) {
        //we need to get the order id number from the url
        val orderId = url.substringAfterLast("/")
        Log.d("TAG", "generateNotificationWithUrl: $orderId")
        //we need to open the order details page
        val args = Bundle()
        args.putString("id", orderId)

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.myOrderDetails, args = args)
            .setComponentName(MainActivity::class.java)

//            .setArguments(args)
            .createPendingIntent()

        //then we need to open show orderID Details


        notificationCounter++

//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.flamingo_profile_icon).setContentTitle(title)
            .setContentText(message).setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent).setAutoCancel(true).setSound(sound)
            .setOnlyAlertOnce(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(notificationCounter, builder.build())
    }

    private fun generateNotification(title: String, message: String) {
        notificationCounter++

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.flamingo_profile_icon).setContentTitle(title)
            .setContentText(message).setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent).setAutoCancel(true).setSound(sound)
            .setOnlyAlertOnce(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(notificationCounter, builder.build())
    }*/
}