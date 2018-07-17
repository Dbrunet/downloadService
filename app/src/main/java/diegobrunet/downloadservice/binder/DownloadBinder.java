package diegobrunet.downloadservice.binder;


import android.app.Notification;
import android.content.Context;
import android.os.Binder;
import android.text.TextUtils;

import diegobrunet.downloadservice.listener.DownloadNotification;
import diegobrunet.downloadservice.task.DownloadTask;
import diegobrunet.downloadservice.util.DownloadUtil;

/**
 * Created by diego on 16/07/18.
 */
public class DownloadBinder extends Binder {

    private DownloadTask downloadTask = null;
    private DownloadNotification downloadNotification = null;
    private String downloadUrl = "";

    public DownloadBinder(Context context) {

        if (downloadNotification == null) {
            downloadNotification = new DownloadNotification(context);
        }
    }

    public void startDownload(String downloadUrl, int progress) {
        //inicia a thread
        downloadTask = new DownloadTask(downloadNotification);

        //set o download task static
        DownloadUtil.setDownloadManager(downloadTask);

        //executa a task
        downloadTask.execute(downloadUrl);

        // Save current download file url.
        this.downloadUrl = downloadUrl;

        // Create and start foreground service with notification.
        downloadNotification.setUrlDownload(downloadUrl);
        Notification notification = downloadNotification.getDownloadNotification("Downloading...", progress);
        downloadNotification.getDownloadService().startForeground(downloadUrl.toString().hashCode(), notification);
    }

    public void continueDownload() {
        if (downloadUrl != null && !TextUtils.isEmpty(downloadUrl)) {
            int lastDownloadProgress = downloadTask.getLastDownloadProgress();
            startDownload(downloadUrl, lastDownloadProgress);
        }
    }

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public void cancelDownload() {
        downloadTask.cancelDownload();
    }

    public void pauseDownload() {
        downloadTask.pauseDownload();
    }

    public DownloadNotification getDownloadNotification() {
        return downloadNotification;
    }
}