package diegobrunet.downloadservice.ui

/**
 * Created by diego on 16/07/18.
 */

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import diegobrunet.downloadservice.binder.DownloadBinder
import diegobrunet.downloadservice.service.DownloadService
import diegobrunet.downloadservice.util.DownloadUtil
import diegobrunet.downloadservice.R

class DownloadActivity : AppCompatActivity(), View.OnClickListener {

    private var etUrlDownload: EditText? = null
    private var btnInitDownload: Button? = null
    private var downloadBinder: DownloadBinder? = null
    private val updateButtonStateHandler: Handler? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            downloadBinder = iBinder as DownloadBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        startAndBindDownloadService()

        //https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4
        //https://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_1mb.mp4
        //https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4

        etUrlDownload = findViewById(R.id.download_manager_url_editor)
        etUrlDownload!!.setText("https://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_1mb.mp4")
        btnInitDownload = findViewById(R.id.download_manager_start_button)
        btnInitDownload!!.setOnClickListener(this)

    }

    override fun onClick(view: View) {

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this@DownloadActivity, "This app need write sdcard permission, please allow.", Toast.LENGTH_LONG).show()
            ActivityCompat.requestPermissions(this@DownloadActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_CODE)
        } else {
            val downloadFileUrl = etUrlDownload?.text.toString().trim { it <= ' ' }
            downloadBinder?.startDownload(downloadFileUrl, 0)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            val grantResult = grantResults[0]
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Liberado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sem permissão!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startAndBindDownloadService() {
        val downloadIntent = Intent(this, DownloadService::class.java)
        startService(downloadIntent)
        bindService(downloadIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }

    companion object {

        private val PERMISSION_CODE = 1
        private val START_BUTTON = 2
    }
}