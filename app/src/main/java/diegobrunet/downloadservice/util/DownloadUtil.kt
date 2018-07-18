package diegobrunet.downloadservice.util

import diegobrunet.downloadservice.task.DownloadTask

object DownloadUtil {

    val TAG_DOWNLOAD = "TAG_DOWNLOAD"

    val DOWNLOAD_SUCCESS = 1

    val DOWNLOAD_FAILED = 2

    var downloadManager: DownloadTask? = null
}