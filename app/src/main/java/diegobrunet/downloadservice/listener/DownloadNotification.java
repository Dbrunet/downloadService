package diegobrunet.downloadservice.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import diegobrunet.downloadservice.service.DownloadService;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by diego on 16/07/18.
 */
public class DownloadNotification {

    private static final int MAX_PROGRESS = 100;
    private static final int MIN_PROGRESS = 0;

    private DownloadService downloadService = null;
    private NotificationCompat.Builder notifyBuilder;
    private NotificationManager notificationManager;

    private int lastProgress = 0;
    private Intent intent;
    private PendingIntent pendingIntent;

    public DownloadNotification() {
        intent = new Intent();
    }

    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
        notificationManager = (NotificationManager) downloadService.getSystemService(NOTIFICATION_SERVICE);
        pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0);
    }

    public DownloadService getDownloadService() {
        return downloadService;
    }

    public void onSuccess() {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download success.", -1);
    }

    public void onFailed() {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download failed.", -1);
    }

    public void onPaused() {
        sendDownloadNotification("Download paused.", lastProgress);
    }

    public void onCanceled() {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download canceled.", -1);
    }

    public void onUpdateDownloadProgress(int progress) {
        try {
            lastProgress = progress;
            sendDownloadNotification("Downloading...", progress);

            // Thread sleep 0.2 seconds to let Pause, Continue and Cancel button in notification clickable.
            Thread.sleep(200);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void sendDownloadNotification(String title, int progress) {
        Notification notification = getDownloadNotification(title, progress);
        notificationManager.notify(1, notification);
    }

    public Notification getDownloadNotification(String title, int progress) {

        if (progress == MIN_PROGRESS) {

            notifyBuilder = new NotificationCompat.Builder(downloadService);

            notifyBuilder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

            Bitmap bitmap = BitmapFactory.decodeResource(downloadService.getResources(), android.R.drawable.stat_sys_download);
            notifyBuilder.setLargeIcon(bitmap);

            notifyBuilder.setContentIntent(pendingIntent);
            notifyBuilder.setContentTitle(title);
            notifyBuilder.setFullScreenIntent(pendingIntent, false);

            Intent pauseDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            pauseDownloadIntent.setAction(DownloadService.ACTION_PAUSE_DOWNLOAD);
            PendingIntent pauseDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, pauseDownloadIntent, 0);
            NotificationCompat.Action pauseDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pausar", pauseDownloadPendingIntent);
            notifyBuilder.addAction(pauseDownloadAction);

            Intent continueDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            continueDownloadIntent.setAction(DownloadService.ACTION_CONTINUE_DOWNLOAD);
            PendingIntent continueDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, continueDownloadIntent, 0);
            NotificationCompat.Action continueDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Continuar", continueDownloadPendingIntent);
            notifyBuilder.addAction(continueDownloadAction);

            Intent cancelDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            cancelDownloadIntent.setAction(DownloadService.ACTION_CANCEL_DOWNLOAD);
            PendingIntent cancelDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, cancelDownloadIntent, 0);
            NotificationCompat.Action cancelDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_delete, "Cancelar", cancelDownloadPendingIntent);
            notifyBuilder.addAction(cancelDownloadAction);
        }

        if (progress > MIN_PROGRESS && progress < MAX_PROGRESS) {

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Download progress ");
            stringBuffer.append(progress);
            stringBuffer.append("%");

            notifyBuilder.setContentText("Download progress " + progress + "%");

            notifyBuilder.setProgress(MAX_PROGRESS, progress, false);

        }

        if (progress == MAX_PROGRESS) {

            notifyBuilder = new NotificationCompat.Builder(downloadService);

            notifyBuilder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

            Bitmap bitmap = BitmapFactory.decodeResource(downloadService.getResources(), android.R.drawable.stat_sys_download);
            notifyBuilder.setLargeIcon(bitmap);

            notifyBuilder.setContentIntent(pendingIntent);
            notifyBuilder.setContentTitle(title);
            notifyBuilder.setFullScreenIntent(pendingIntent, false);
        }

        Notification notification = notifyBuilder.build();

        return notification;
    }

}
