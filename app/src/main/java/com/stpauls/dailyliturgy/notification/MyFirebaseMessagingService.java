package com.stpauls.dailyliturgy.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stpauls.dailyliturgy.MainActivity;
import com.stpauls.dailyliturgy.R;
import com.stpauls.dailyliturgy.base.App;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessag";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "onMessageReceived: " + remoteMessage.toString());

        try {
            if (remoteMessage.getData().size() > 0) {

                JSONObject object = new JSONObject(remoteMessage.getData());
                sendNotification(object);

            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("title", remoteMessage.getNotification().getTitle());
                jsonObject.put("body", remoteMessage.getNotification().getBody());
                sendNotification(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onNewToken(String token) {
    }

    private void sendNotification(JSONObject bean) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.get(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(App.get(), channelId)
                        .setSmallIcon(R.drawable.st_pauls_icon)
                        .setContentTitle(bean.optString("title"))
                        .setContentText(bean.optString("body"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "DailyLiturgy",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


    }
}