package diegobrunet.downloadservice.task;

import android.os.AsyncTask;

import java.io.File;

import diegobrunet.downloadservice.listener.DownloadNotification;
import diegobrunet.downloadservice.util.DownloadUtil;
import diegobrunet.downloadservice.util.FileUtil;

import static diegobrunet.downloadservice.util.FileUtil.createDownloadLocalFile;

public class DownloadTask extends AsyncTask<String, Integer, Integer> {

    private DownloadNotification downloadListener = null;

    private boolean downloadCanceled = false;
    private boolean downloadPaused = false;
    private int lastDownloadProgress = 0;
    private String currDownloadUrl = "";

    public DownloadTask(DownloadNotification downloadListener) {
        this.downloadListener = downloadListener;
        this.setDownloadPaused(false);
        this.setDownloadCanceled(false);
    }

    @Override
    protected Integer doInBackground(String... params) {

        // Defina a prioridade baixa que a prioridade para que as ações Pausa, Continuar e Cancelar
        // da linha principal não sejam bloqueadas.
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 2);

        String downloadFileUrl = "";
        if (params != null && params.length > 0) {
            downloadFileUrl = params[0];
        }

        this.currDownloadUrl = downloadFileUrl;

        //arquivo criado na pasta download
        File downloadLocalFile = createDownloadLocalFile(downloadFileUrl);
        //retorna o status do download
        int ret = FileUtil.downloadFileFromUrl(downloadFileUrl, downloadLocalFile);

        return ret;
    }

    @Override
    protected void onPostExecute(Integer downloadStatue) {
        if (downloadStatue == DownloadUtil.DOWNLOAD_SUCCESS) {
            this.setDownloadCanceled(false);
            this.setDownloadPaused(false);
            downloadListener.onSuccess();
        } else if (downloadStatue == DownloadUtil.DOWNLOAD_FAILED) {
            this.setDownloadCanceled(false);
            this.setDownloadPaused(false);
            downloadListener.onFailed();
        } else if (downloadStatue == DownloadUtil.DOWNLOAD_PAUSED) {
            downloadListener.onPaused();
        } else if (downloadStatue == DownloadUtil.DOWNLOAD_CANCELED) {
            downloadListener.onCanceled();
        }
    }

    /* Update download async task progress. */
    public void updateTaskProgress(Integer newDownloadProgress) {
        lastDownloadProgress = newDownloadProgress;
        downloadListener.onUpdateDownloadProgress(newDownloadProgress);
    }

    public boolean isDownloadCanceled() {
        return downloadCanceled;
    }

    public void setDownloadCanceled(boolean downloadCanceled) {
        this.downloadCanceled = downloadCanceled;
    }

    public boolean isDownloadPaused() {
        return downloadPaused;
    }

    public void setDownloadPaused(boolean downloadPaused) {
        this.downloadPaused = downloadPaused;
    }

    public int getLastDownloadProgress() {
        return lastDownloadProgress;
    }

    public void setLastDownloadProgress(int lastDownloadProgress) {
        this.lastDownloadProgress = lastDownloadProgress;
    }

    public void pauseDownload() {
        this.setDownloadPaused(true);
    }

    public void cancelDownload() {
        this.setDownloadCanceled(true);
    }
}
