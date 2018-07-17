package diegobrunet.downloadservice.ui;

/**
 * Created by diego on 16/07/18.
 */

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import diegobrunet.downloadservice.binder.DownloadBinder;
import diegobrunet.downloadservice.service.DownloadService;
import diegobrunet.downloadservice.util.DownloadUtil;
import diegobrunet.downloadservice.R;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_CODE = 1;
    private static final int START_BUTTON = 2;

    private EditText etUrlDownload = null;
    private Button btnInitDownload = null;
    private DownloadBinder downloadBinder = null;
    private Handler updateButtonStateHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        startAndBindDownloadService();

        //https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4
        //https://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_1mb.mp4
        //https://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4

        etUrlDownload = findViewById(R.id.download_manager_url_editor);
        etUrlDownload.setText("https://www.sample-videos.com/video/mp4/480/big_buck_bunny_480p_1mb.mp4");
        btnInitDownload = findViewById(R.id.download_manager_start_button);
        btnInitDownload.setOnClickListener(this);

        if (updateButtonStateHandler == null) {
            updateButtonStateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == START_BUTTON) {
                        btnInitDownload.setEnabled(true);
                    }
                }
            };
        }
    }

    @Override
    public void onClick(View view) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(DownloadActivity.this, "This app need write sdcard permission, please allow.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else {

            String downloadFileUrl = etUrlDownload.getText().toString().trim();
            downloadBinder.startDownload(downloadFileUrl, 0);
            btnInitDownload.setEnabled(false);

            Thread enableButtonThread = new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            if (downloadBinder.getDownloadTask().isDownloadCanceled()) {
                                Message msg = new Message();
                                msg.what = START_BUTTON;
                                updateButtonStateHandler.sendMessage(msg);
                                break;
                            }

                            Thread.sleep(1000);
                        } catch (Exception ex) {
                            Log.e(DownloadUtil.TAG_DOWNLOAD_MANAGER, ex.getMessage(), ex);
                        }
                    }
                }
            };
            enableButtonThread.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            int grantResult = grantResults[0];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Liberado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sem permissão!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void startAndBindDownloadService() {
        Intent downloadIntent = new Intent(this, DownloadService.class);
        startService(downloadIntent);
        bindService(downloadIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}