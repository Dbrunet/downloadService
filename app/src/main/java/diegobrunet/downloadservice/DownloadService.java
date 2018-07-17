package diegobrunet.downloadservice;

/**
 * Created by diego on 16/07/18.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class DownloadService extends Service {

    public static final String ACTION_PAUSE_DOWNLOAD = "ACTION_PAUSE_DOWNLOAD";

    public static final String ACTION_CONTINUE_DOWNLOAD = "ACTION_CONTINUE_DOWNLOAD";

    public static final String ACTION_CANCEL_DOWNLOAD = "ACTION_CANCEL_DOWNLOAD";

    private DownloadBinder downloadBinder = new DownloadBinder();

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        downloadBinder.getDownloadListener().setDownloadService(this);
        return downloadBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (ACTION_PAUSE_DOWNLOAD.equals(action)) {
            downloadBinder.pauseDownload();
            Toast.makeText(getApplicationContext(), "Download is paused", Toast.LENGTH_LONG).show();
        } else if (ACTION_CANCEL_DOWNLOAD.equals(action)) {
            downloadBinder.cancelDownload();
            Toast.makeText(getApplicationContext(), "Download is canceled", Toast.LENGTH_LONG).show();
        } else if (ACTION_CONTINUE_DOWNLOAD.equals(action)) {
            downloadBinder.continueDownload();
            Toast.makeText(getApplicationContext(), "Download continue", Toast.LENGTH_LONG).show();
        }

        return super.onStartCommand(intent, flags, startId);
    }
}