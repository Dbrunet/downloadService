package diegobrunet.downloadservice;

/**
 * Created by diego on 16/07/18.
 */

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Jerry on 3/9/2018.
 */

public class DownloadUtil {

    public static final String TAG_DOWNLOAD_MANAGER = "TAG_DOWNLOAD_MANAGER";

    public static final int DOWNLOAD_SUCCESS = 1;

    public static final int DOWNLOAD_FAILED = 2;

    public static final int DOWNLOAD_PAUSED = 3;

    public static final int DOWNLOAD_CANCELED = 4;

    private static DownloadManager downloadManager = null;

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public static void setDownloadManager(DownloadManager downloadManager) {
        DownloadUtil.downloadManager = downloadManager;
    }

    /* Get download file size returned from http server header. */
    public static long getDownloadUrlFileSize(String downloadUrl) {
        long ret = 0;

        try {
            if (downloadUrl != null && !TextUtils.isEmpty(downloadUrl)) {
                Request.Builder builder = new Request.Builder();
                builder = builder.url(downloadUrl);
                Request request = builder.build();

                Call call = okHttpClient.newCall(request);
                Response response = call.execute();

                if (response != null) {
                    if (response.isSuccessful()) {
                        String contentLength = response.header("Content-Length");
                        ret = Long.parseLong(contentLength);
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG_DOWNLOAD_MANAGER, ex.getMessage(), ex);
        } finally {
            return ret;
        }
    }


    public static int downloadFileFromUrl(String downloadFileUrl, File existLocalFile) {
        int ret = DOWNLOAD_SUCCESS;
        try {

            long downloadFileLength = getDownloadUrlFileSize(downloadFileUrl);

            long existLocalFileLength = existLocalFile.length();

            if (downloadFileLength == 0) {
                ret = DOWNLOAD_FAILED;
            } else if (downloadFileLength == existLocalFileLength) {
                ret = DOWNLOAD_SUCCESS;
            } else {

                Request.Builder builder = new Request.Builder();
                builder = builder.url(downloadFileUrl);
                builder = builder.addHeader("RANGE", "bytes=" + existLocalFileLength);
                Request request = builder.build();

                Call call = okHttpClient.newCall(request);
                Response response = call.execute();

                if (response != null && response.isSuccessful()) {
                    RandomAccessFile downloadFile = new RandomAccessFile(existLocalFile, "rw");
                    downloadFile.seek(existLocalFileLength);

                    ResponseBody responseBody = response.body();
                    InputStream inputStream = responseBody.byteStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    byte data[] = new byte[102400];

                    long totalReadLength = 0;

                    int readLength = bufferedInputStream.read(data);

                    while (readLength != -1) {

                        if (getDownloadManager().isDownloadPaused()) {
                            ret = DOWNLOAD_PAUSED;
                            break;
                        } else if (getDownloadManager().isDownloadCanceled()) {
                            ret = DOWNLOAD_CANCELED;
                            break;
                        } else {

                            downloadFile.write(data, 0, readLength);

                            totalReadLength = totalReadLength + readLength;

                            int downloadProgress = (int) ((totalReadLength + existLocalFileLength) * 100 / downloadFileLength);

                            getDownloadManager().updateTaskProgress(downloadProgress);

                            readLength = bufferedInputStream.read(data);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            Log.e(TAG_DOWNLOAD_MANAGER, ex.getMessage(), ex);
        } finally {
            return ret;
        }
    }
}