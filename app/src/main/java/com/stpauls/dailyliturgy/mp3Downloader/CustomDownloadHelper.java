package com.stpauls.dailyliturgy.mp3Downloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomDownloadHelper {

    private CustomDownloadHelper(){}

    private static CustomDownloadHelper instance;

    private static final String CHANNEL_ID = "download_channel";
    private static final int NOTIFICATION_ID = 1001;

    public static CustomDownloadHelper getInstance() {
        if (instance != null){
            return instance;
        }
        instance = new CustomDownloadHelper();
        return instance;
    }

    public interface DownloadCallback {
        void onProgress(long downloaded, long total);
        void onCompleted(File file);
        void onError(Exception e);
    }

    public void downloadFile(Context context, String urlString, File destination, DownloadCallback callback) {
        createNotificationChannel(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        new Thread(() -> {
            HttpURLConnection connection = null;
            InputStream input = null;
            FileOutputStream output = null;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Server returned HTTP " + responseCode);
                }

                int fileLength = connection.getContentLength();
                input = new BufferedInputStream(connection.getInputStream());
                output = new FileOutputStream(destination);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                boolean indeterminate = fileLength <= 0;
                // Show initial notification
                showProgressNotification(context, notificationManager, 0, fileLength, indeterminate);
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                    long finalTotal = total;
                    mainHandler.post(() -> {
                        callback.onProgress(finalTotal, fileLength);
                        showProgressNotification(context, notificationManager, finalTotal, fileLength, indeterminate);
                    });
                }
                output.flush();
                mainHandler.post(() -> {
                    callback.onCompleted(destination);
                    removeNotification(notificationManager);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    callback.onError(e);
                    removeNotification(notificationManager);
                });
            } finally {
                try { if (output != null) output.close(); } catch (IOException ignored) {}
                try { if (input != null) input.close(); } catch (IOException ignored) {}
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Download Progress",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows download progress");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showProgressNotification(Context context, NotificationManager notificationManager, long progress, long total, boolean indeterminate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Downloading file")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                .setOnlyAlertOnce(true);
        if (indeterminate) {
            builder.setProgress(0, 0, true);
        } else {
            builder.setProgress((int) total, (int) progress, false)
                    .setContentText((int) (progress * 100 / total) + "%");
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void removeNotification(NotificationManager notificationManager) {
        notificationManager.cancel(NOTIFICATION_ID);
    }
} 