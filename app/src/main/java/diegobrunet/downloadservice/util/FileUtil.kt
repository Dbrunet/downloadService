package diegobrunet.downloadservice.util

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import diegobrunet.downloadservice.util.DownloadUtil.DOWNLOAD_FAILED
import diegobrunet.downloadservice.util.DownloadUtil.DOWNLOAD_SUCCESS
import diegobrunet.downloadservice.util.DownloadUtil.TAG_DOWNLOAD
import diegobrunet.downloadservice.util.DownloadUtil.downloadManager
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

object FileUtil {

    private val okHttpClient = OkHttpClient()

    //baixa o arquivo e copia para o arquivo local criado
    fun downloadFileFromUrl(downloadFileUrl: String, existLocalFile: File): Int {
        var ret = DOWNLOAD_SUCCESS

        try {

            //atribui o tamanho do arquivo
            val downloadFileLength = getRequestFileSize(downloadFileUrl)
            //verifica se o tamanho do arquivo é o mesmo do arquivo local
            val existLocalFileLength = existLocalFile.length()

            //verifica o tamanho do arquivo
            if (downloadFileLength == 0L) {
                ret = DOWNLOAD_FAILED
            } else if (downloadFileLength == existLocalFileLength) {
                ret = DOWNLOAD_SUCCESS
            } else {

                //faz o stream do arquivo e
                var builder = Request.Builder()
                builder = builder.url(downloadFileUrl)
                builder = builder.addHeader("RANGE", "bytes=" + existLocalFileLength)
                val request = builder.build()

                val call = okHttpClient.newCall(request)
                val response = call.execute()

                if (response != null && response.isSuccessful) {

                    val downloadFile = RandomAccessFile(existLocalFile, "rw")
                    downloadFile.seek(existLocalFileLength)

                    val responseBody = response.body()
                    val inputStream = responseBody!!.byteStream()
                    val bufferedInputStream = BufferedInputStream(inputStream)

                    val data = ByteArray(102400)

                    var totalReadLength: Long = 0

                    var readLength = bufferedInputStream.read(data)

                    while (readLength != -1) {

                        downloadFile.write(data, 0, readLength)

                        totalReadLength = totalReadLength + readLength

                        val downloadProgress = ((totalReadLength + existLocalFileLength) * 100 / downloadFileLength).toInt()

                        downloadManager!!.updateTaskProgress(downloadProgress)

                        readLength = bufferedInputStream.read(data)
                    }
                }
            }

        } catch (ex: Exception) {
            Log.e(TAG_DOWNLOAD, ex.message, ex)
        } finally {
            return ret
        }
    }

    //retorna o tamanho do arquivo
    fun getRequestFileSize(downloadUrl: String?): Long {
        var ret: Long = 0

        try {
            if (downloadUrl != null && !TextUtils.isEmpty(downloadUrl)) {

                var builder = Request.Builder()
                builder = builder.url(downloadUrl)
                val request = builder.build()

                val call = okHttpClient.newCall(request)
                val response = call.execute()

                if (response != null) {
                    if (response.isSuccessful) {
                        val contentLength = response.header("Content-Length")
                        ret = java.lang.Long.parseLong(contentLength)
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG_DOWNLOAD, ex.message, ex)
        } finally {
            return ret
        }
    }

    //cria e retorna o arquivo local
    fun createDownloadLocalFile(downloadFileUrl: String?): File? {
        var file: File? = null

        try {
            if (downloadFileUrl != null && !TextUtils.isEmpty(downloadFileUrl)) {
                val lastIndex = downloadFileUrl.lastIndexOf("/")
                if (lastIndex > -1) {
                    val downloadFileName = downloadFileUrl.substring(lastIndex + 1)

                    /** TODO Apontando para o diretório de Download **/
                    val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val downloadDirectoryPath = downloadDirectory.path

                    file = File(downloadDirectoryPath + "/" + downloadFileName)

                    if (!file.exists()) {
                        file.createNewFile()
                    }
                }
            }
        } catch (ex: IOException) {
            Log.e(DownloadUtil.TAG_DOWNLOAD, ex.message, ex)
        } finally {
            return file
        }
    }
}
