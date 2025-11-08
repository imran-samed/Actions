package com.stpauls.dailyliturgy.mp3Downloader;

import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.stpauls.dailyliturgy.Global;
import com.stpauls.dailyliturgy.base.App;
import com.stpauls.dailyliturgy.others.Callback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FetchHelper {

    private static final String TAG = "FetchHelp";
    private static FetchHelper instance;

    public synchronized static FetchHelper getInstance() {
        if (instance == null) {
            synchronized (FetchHelper.class) {
                if (instance == null) {
                    instance = new FetchHelper();
                }
            }
        }
        return instance;
    }

    public void downloadUrl(Context context, String url, Callback<Boolean> param) {
        String[] index = url.split("/");
        String fileName = index[index.length - 1];
        File folder = Global.INSTANCE.getPOPULAR_HYMNS_FOLDER_PATH();
        folder.mkdir();
        File appSpecificExternalDir = new File(folder, fileName);
        try {
            appSpecificExternalDir.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        CustomDownloadHelper.DownloadCallback callback1 = new CustomDownloadHelper.DownloadCallback() {
            @Override
            public void onProgress(long downloaded, long total) {}

            @Override
            public void onCompleted(File file) {
                param.onSuccess(true);
            }

            @Override
            public void onError(Exception e) {
                param.onSuccess(false);
            }
        };
        CustomDownloadHelper.getInstance().downloadFile(context, url, appSpecificExternalDir, callback1);
    }

    public Set<String> getListFiles(File parentDir) {
        Set<String> inFiles = new HashSet();
        File[] files = parentDir.listFiles();
        if (files == null || files.length <= 0) {
            Log.d(TAG, "No file available");
            return inFiles;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getListFiles(file));
            } else {
                inFiles.add(file.getPath());
                Log.d(TAG, "FilesName = [" + file.getPath() + "]");
            }
        }
        return inFiles;
    }
}
