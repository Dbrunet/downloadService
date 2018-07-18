package diegobrunet.downloadservice.task

import android.os.AsyncTask

import java.io.File

import diegobrunet.downloadservice.listener.DownloadNotification
import diegobrunet.downloadservice.util.DownloadUtil
import diegobrunet.downloadservice.util.FileUtil

import diegobrunet.downloadservice.util.FileUtil.createDownloadLocalFile

class DownloadTask(downloadListener: DownloadNotification) : AsyncTask<String, Int, Int>() {

    private var downloadListener: DownloadNotification? = null

    var lastDownloadProgress = 0
    private var currDownloadUrl = ""

    init {
        this.downloadListener = downloadListener
    }

    override fun doInBackground(vararg params: String): Int? {

        // Defina a prioridade baixa que a prioridade para que as ações Pausa, Continuar e Cancelar
        // da linha principal não sejam bloqueadas.
        Thread.currentThread().priority = Thread.NORM_PRIORITY - 2

        var downloadFileUrl = ""
        if (params != null && params.size > 0) {
            downloadFileUrl = params[0]
        }

        this.currDownloadUrl = downloadFileUrl

        //arquivo criado na pasta download
        val downloadLocalFile = createDownloadLocalFile(downloadFileUrl)
        //retorna o status do download

        return FileUtil.downloadFileFromUrl(downloadFileUrl, downloadLocalFile!!)
    }

    override fun onPostExecute(downloadStatue: Int?) {
        if (downloadStatue == DownloadUtil.DOWNLOAD_SUCCESS) {
            downloadListener?.onSuccess()
        } else if (downloadStatue == DownloadUtil.DOWNLOAD_FAILED) {
            downloadListener?.onFailed()
        }
    }

    /* Update download async task progress. */
    fun updateTaskProgress(newDownloadProgress: Int?) {
        lastDownloadProgress = newDownloadProgress!!
        downloadListener?.onUpdateDownloadProgress(newDownloadProgress)
    }
}
