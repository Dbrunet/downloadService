package diegobrunet.downloadservice.listener;

/**
 * Created by diego on 16/07/18.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import diegobrunet.downloadservice.R;
import diegobrunet.downloadservice.service.DownloadService;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Jerry on 3/9/2018.
 */

public class DownloadNotification {

    String CHANNEL_ID = "CHANNEL_ID";
    private static final int MAX_PROGRESS = 100;
    private static final int MIN_PROGRESS = 0;

    private DownloadService downloadService = null;
    private NotificationCompat.Builder notifyBuilder;
    private int lastProgress = 0;
    private String urlDownload;

    public DownloadNotification(Context context) {
    }

    public void onUpdateDownloadProgress(int progress) {
        try {

            lastProgress = progress;
            sendDownloadNotification("Downloading...", progress);

            Thread.sleep(200);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendDownloadNotification(String title, int progress) {
        Notification notification = getDownloadNotification(title, progress);

        NotificationManager notificationManager = (NotificationManager) downloadService.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(urlDownload.toString().hashCode(), notification);

    }

    public Notification getDownloadNotification(String title, int progress) {

        // Create NotificationManager.
        final NotificationManager notificationManager = (NotificationManager) downloadService.getSystemService(Context.NOTIFICATION_SERVICE);

        // NotificationTargetActivity is the activity opened when user click notification.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0);

        // Create a Notification Builder instance.
        String textContent = "Download progress...";
        int smallIconResId = R.drawable.ic_launcher_foreground;
        int largeIconResId = R.drawable.ic_launcher_background;
        long sendTime = System.currentTimeMillis();
        boolean autoCancel = false;

        // Get general settings Builder instance.
        final NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, smallIconResId, largeIconResId, autoCancel, sendTime);

        // Set content intent.
        builder.setContentIntent(pendingIntent);

        // Use both light, sound and vibrate.
        builder.setDefaults(Notification.DEFAULT_ALL);

        // The thread object will update the Notification progress programmatically.
        Notification notification = null;

        if (progress < MAX_PROGRESS) {

            builder.setProgress(MAX_PROGRESS, progress, false);
            // Create Notification instance.
            notification = builder.build();
            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            notificationManager.notify(1, notification = builder.build());

        } else if (progress == MAX_PROGRESS) {

            builder.setContentText("Download complete");
            //.builder.setProgress(0, 0, true) will show a indeterminate progress bar.
            builder.setProgress(MAX_PROGRESS, 0, false);
            // Send the notification.
            // Create Notification instance.
            notificationManager.notify(1, notification = builder.build());
        }

        return notification;
    }

    // This method create and return a general Notification Builder instance.
    private NotificationCompat.Builder getGeneralNotificationBuilder(String title, String textContent, int smallIconResId, int largeIconResId, boolean autoCancel, long sendTime) {
        // Create a Notification Builder instance.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(downloadService.getApplicationContext());

        // Set small icon.
        builder.setSmallIcon(smallIconResId);

        // Set large icon.
        Bitmap bitmap = BitmapFactory.decodeResource(downloadService.getResources(), largeIconResId);
        builder.setLargeIcon(bitmap);

        // Set title.
        builder.setContentTitle(title);

        // Set content text.
        builder.setContentText(textContent);

        // Set notification send time.
        builder.setWhen(sendTime);

        // If true then cancel the notification automatically.
        builder.setAutoCancel(autoCancel);

        return builder;
    }

    public Notification getNotification(String title, int progress) {
        //sera criado uma notificação por URL
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(downloadService);
        notifyBuilder.setSmallIcon(R.mipmap.ic_launcher);

        Bitmap bitmap = BitmapFactory.decodeResource(downloadService.getResources(), R.drawable.ic_launcher_foreground);
        notifyBuilder.setLargeIcon(bitmap);

        // NotificationTargetActivity is the activity opened when user click notification.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0);

        notifyBuilder.setContentIntent(pendingIntent);
        notifyBuilder.setContentTitle(title);
        notifyBuilder.setAutoCancel(true);
        notifyBuilder.setFullScreenIntent(pendingIntent, true);

        if (progress > MIN_PROGRESS && progress < MAX_PROGRESS) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Download progress ");
            stringBuffer.append(progress);
            stringBuffer.append("%");

            notifyBuilder.setContentText("Download progress " + progress + "%");

            notifyBuilder.setProgress(MAX_PROGRESS, progress, false);

            // Add Pausar
            Intent pauseDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            pauseDownloadIntent.setAction(DownloadService.ACTION_PAUSE_DOWNLOAD);
            PendingIntent pauseDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, pauseDownloadIntent, 0);
            NotificationCompat.Action pauseDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pausar", pauseDownloadPendingIntent);
            notifyBuilder.addAction(pauseDownloadAction);

            // Add Continuar
            Intent continueDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            continueDownloadIntent.setAction(DownloadService.ACTION_CONTINUE_DOWNLOAD);
            PendingIntent continueDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, continueDownloadIntent, 0);
            NotificationCompat.Action continueDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_media_play, "Continuar", continueDownloadPendingIntent);
            notifyBuilder.addAction(continueDownloadAction);

            // Add Cancelar
            Intent cancelDownloadIntent = new Intent(getDownloadService(), DownloadService.class);
            cancelDownloadIntent.setAction(DownloadService.ACTION_CANCEL_DOWNLOAD);
            PendingIntent cancelDownloadPendingIntent = PendingIntent.getService(getDownloadService(), 0, cancelDownloadIntent, 0);
            NotificationCompat.Action cancelDownloadAction = new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel, "Cancelar", cancelDownloadPendingIntent);
            notifyBuilder.addAction(cancelDownloadAction);
        }

        Notification notification = notifyBuilder.build();

        return notification;
    }

    public DownloadService getDownloadService() {
        return downloadService;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
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

}
