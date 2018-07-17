package diegobrunet.downloadservice;

/**
 * Created by diego on 16/07/18.
 */

import android.app.Notification;
import android.os.Binder;
import android.text.TextUtils;

/**
 * Created by Jerry on 3/9/2018.
 */

public class DownloadBinder extends Binder {

    private DownloadManager downloadManager = null;

    private DownloadListener downloadListener = null;

    private String currDownloadUrl = "";

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public DownloadBinder() {

        if (downloadListener == null) {
            downloadListener = new DownloadListener();
        }
    }

    public void startDownload(String downloadUrl, int progress) {
        /* Because downloadManager is a subclass of AsyncTask, and AsyncTask can only be executed once,
        * So each download need a new downloadManager. */
        downloadManager = new DownloadManager(downloadListener);

        /* Because DownloadUtil has a static variable of downloadManger, so each download need to use new downloadManager. */
        DownloadUtil.setDownloadManager(downloadManager);

        // Execute download manager, this will invoke downloadManager's doInBackground() method.
        downloadManager.execute(downloadUrl);

        // Save current download file url.
        currDownloadUrl = downloadUrl;

        // Create and start foreground service with notification.
        Notification notification = downloadListener.getDownloadNotification("Downloading...", progress);
        downloadListener.getDownloadService().startForeground(1, notification);
    }

    public void continueDownload() {
        if (currDownloadUrl != null && !TextUtils.isEmpty(currDownloadUrl)) {
            int lastDownloadProgress = downloadManager.getLastDownloadProgress();
            startDownload(currDownloadUrl, lastDownloadProgress);
        }
    }

    public void cancelDownload() {
        downloadManager.cancelDownload();
    }

    public void pauseDownload() {
        downloadManager.pauseDownload();
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }
}