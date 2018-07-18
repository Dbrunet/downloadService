package diegobrunet.downloadservice.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

import diegobrunet.downloadservice.binder.DownloadBinder

/**
 * Created by diego on 16/07/18.
 */
class DownloadService : Service() {

    private val downloadBinder = DownloadBinder()

    override fun onBind(intent: Intent): IBinder? {
        downloadBinder.downloadNotification?.downloadService = this
        return downloadBinder
    }
}