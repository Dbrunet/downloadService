package diegobrunet.downloadservice.util;

import diegobrunet.downloadservice.task.DownloadTask;

public class DownloadUtil {

    public static final String TAG_DOWNLOAD_MANAGER = "TAG_DOWNLOAD_MANAGER";

    public static final int DOWNLOAD_SUCCESS = 1;

    public static final int DOWNLOAD_FAILED = 2;

    public static final int DOWNLOAD_PAUSED = 3;

    public static final int DOWNLOAD_CANCELED = 4;

    private static DownloadTask downloadManager = null;

    public static DownloadTask getDownloadManager() {
        return downloadManager;
    }

    public static void setDownloadManager(DownloadTask downloadManager) {
        DownloadUtil.downloadManager = downloadManager;
    }
}