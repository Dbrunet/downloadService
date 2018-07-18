package diegobrunet.downloadservice.binder


import android.os.Binder
import diegobrunet.downloadservice.listener.DownloadNotification
import diegobrunet.downloadservice.task.DownloadTask
import diegobrunet.downloadservice.util.DownloadUtil

/**
 * Created by diego on 16/07/18.
 */
class DownloadBinder : Binder() {

    var downloadTask: DownloadTask? = null
    var downloadNotification: DownloadNotification? = null
    private var downloadUrl: String? = ""

    init {
        if (downloadNotification == null) {
            downloadNotification = DownloadNotification()
        }
    }

    fun startDownload(downloadUrl: String, progress: Int) {
        //inicia a thread
        downloadTask = DownloadTask(downloadNotification!!)

        //set o download task static
        DownloadUtil.downloadManager = downloadTask

        //executa a task
        downloadTask?.execute(downloadUrl)

        // Corrente url
        this.downloadUrl = downloadUrl

        //cria notificação e insere em fore
        val notification = downloadNotification?.getDownloadNotification("Baixando...", null, progress)
        downloadNotification!!.downloadService?.startForeground(1, notification)
    }

}