package diegobrunet.downloadservice.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static diegobrunet.downloadservice.util.DownloadUtil.DOWNLOAD_CANCELED;
import static diegobrunet.downloadservice.util.DownloadUtil.DOWNLOAD_FAILED;
import static diegobrunet.downloadservice.util.DownloadUtil.DOWNLOAD_PAUSED;
import static diegobrunet.downloadservice.util.DownloadUtil.DOWNLOAD_SUCCESS;
import static diegobrunet.downloadservice.util.DownloadUtil.TAG_DOWNLOAD_MANAGER;
import static diegobrunet.downloadservice.util.DownloadUtil.getDownloadManager;

public class FileUtil {

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static int downloadFileFromUrl(String downloadFileUrl, File existLocalFile) {
        int ret = DOWNLOAD_SUCCESS;

        try {

            //atribui o tamanho do arquivo
            long downloadFileLength = getRequestFileSize(downloadFileUrl);
            //verifica se o tamanho do arquivo é o mesmo do arquivo local
            long existLocalFileLength = existLocalFile.length();

            //verifica o tamanho do arquivo
            if (downloadFileLength == 0) {
                ret = DOWNLOAD_FAILED;
            } else if (downloadFileLength == existLocalFileLength) {
                ret = DOWNLOAD_SUCCESS;
            } else {

                //faz o stream do arquivo e
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

    /* Get download file size returned from http server header. */
    public static long getRequestFileSize(String downloadUrl) {
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

    public static File createDownloadLocalFile(String downloadFileUrl) {
        File file = null;

        try {
            if (downloadFileUrl != null && !TextUtils.isEmpty(downloadFileUrl)) {
                int lastIndex = downloadFileUrl.lastIndexOf("/");
                if (lastIndex > -1) {
                    String downloadFileName = downloadFileUrl.substring(lastIndex + 1);
                    String downloadDirectoryName = Environment.DIRECTORY_DOWNLOADS;
                    File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    String downloadDirectoryPath = downloadDirectory.getPath();

                    file = new File(downloadDirectoryPath + "/" + downloadFileName);

                    if (!file.exists()) {
                        file.createNewFile();
                    }
                }
            }
        } catch (IOException ex) {
            Log.e(DownloadUtil.TAG_DOWNLOAD_MANAGER, ex.getMessage(), ex);
        } finally {
            return file;
        }
    }
}
