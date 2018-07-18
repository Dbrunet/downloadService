package diegobrunet.downloadservice.listener

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat

import diegobrunet.downloadservice.service.DownloadService

import android.content.Context.NOTIFICATION_SERVICE

/**
 * Created by diego on 16/07/18.
 */
class DownloadNotification {

    var downloadService: DownloadService? = null
        set(downloadService) {
            field = downloadService

            intent = Intent()
            pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0)
            notifyBuilder = NotificationCompat.Builder(downloadService)
        }

    private var lastProgress = 0
    private var intent: Intent? = null
    private var pendingIntent: PendingIntent? = null
    private var notifyBuilder: NotificationCompat.Builder? = null

    fun onSuccess() {
        this.downloadService?.stopForeground(true)
        sendDownloadNotification("Baixado com sucesso.", "finalizado", -1)
    }

    fun onFailed() {
        this.downloadService?.stopForeground(true)
        sendDownloadNotification("O Download falhou.", "finalizado", -1)
    }

    fun onUpdateDownloadProgress(progress: Int) {
        try {
            lastProgress = progress
            sendDownloadNotification("Baixando...", "Iniciando o serviço", progress)

            Thread.sleep(200)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun sendDownloadNotification(title: String, text: String?, progress: Int) {
        val notification = getDownloadNotification(title, text, progress)

        val notificationManager = this.downloadService!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    fun getDownloadNotification(title: String, text: String?, progress: Int): Notification {

        if (progress == 0) {
            val bitmap = BitmapFactory.decodeResource(this.downloadService!!.resources, android.R.drawable.stat_sys_download)
            notifyBuilder?.setLargeIcon(bitmap)
            notifyBuilder?.setSmallIcon(android.R.mipmap.sym_def_app_icon)
            notifyBuilder?.setAutoCancel(true)
            notifyBuilder?.setContentIntent(pendingIntent)
            notifyBuilder?.setContentTitle(title)
            if (text != null) notifyBuilder?.setContentText(text)
            notifyBuilder?.priority = NotificationCompat.PRIORITY_HIGH
        }

        if (progress in 1..99) {
            val stringBuffer = StringBuffer()
            stringBuffer.append("Baixando progresso ")
            stringBuffer.append(progress)
            stringBuffer.append("%")

            notifyBuilder?.setContentText("Baixando progresso $progress%")
            notifyBuilder?.setProgress(100, progress, false)
        }
        if (progress == -1) {
            notifyBuilder?.setContentTitle(title)
            if (text != null) notifyBuilder?.setContentText(text)
            notifyBuilder?.setProgress(0, 0, false)
        }

        return notifyBuilder!!.build()
    }
}
