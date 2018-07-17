package diegobrunet.downloadservice;

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

public class DownloadActivity extends AppCompatActivity {

    private EditText downloadUrlEditor = null;

    private Button startDownloadButton = null;

    private DownloadBinder downloadBinder = null;

    private int REQUEST_WRITE_PERMISSION_CODE = 1;

    private Handler updateButtonStateHandler = null;

    private int MESSAGE_UPDATE_START_BUTTON = 2;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        setTitle("dev2qa.com - Android Download Manager Example.");

        startAndBindDownloadService();

        initControls();

        startDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(DownloadActivity.this, "This app need write sdcard permission, please allow.", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION_CODE);
                } else {
                    String downloadFileUrl = downloadUrlEditor.getText().toString();
                    downloadBinder.startDownload(downloadFileUrl, 0);
                    startDownloadButton.setEnabled(false);

                    Thread enableButtonThread = new Thread() {
                        @Override
                        public void run() {
                            while (true) {
                                try {
                                    if (downloadBinder.getDownloadManager().isDownloadCanceled()) {
                                        Message msg = new Message();
                                        msg.what = MESSAGE_UPDATE_START_BUTTON;
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
        });
    }

    private void initControls() {

        if (downloadUrlEditor == null) {
            downloadUrlEditor = (EditText) findViewById(R.id.download_manager_url_editor);
            downloadUrlEditor.setText("http://dev2qa.com/demo/media/play_video_test.mp4");
        }

        if (startDownloadButton == null) {
            startDownloadButton = (Button) findViewById(R.id.download_manager_start_button);
        }

        if (updateButtonStateHandler == null) {
            updateButtonStateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == MESSAGE_UPDATE_START_BUTTON) {
                        startDownloadButton.setEnabled(true);
                    }
                }
            };
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION_CODE) {
            int grantResult = grantResults[0];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can continue to use this app.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You disallow write external storage permission, app closed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

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