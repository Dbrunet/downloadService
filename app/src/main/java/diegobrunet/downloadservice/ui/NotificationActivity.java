//package diegobrunet.downloadservice.ui;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.os.Bundle;
//import android.support.v4.app.NotificationCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RemoteViews;
//
//import diegobrunet.downloadservice.R;
//
///**
// * Created by labtime on 17/07/18.
// */
//
//public class NotificationActivity extends AppCompatActivity {
//
//    // Constants variable value that is used to identify different notification.
//    private int NOTIFICATION_ID_NORMAL_SIZE = 1;
//
//    private int NOTIFICATION_ID_LARGE_SIZE = 2;
//
//    private int NOTIFICATION_ID_PROGRESS_BAR = 3;
//
//    private int NOTIFICATION_ID_HEADS_UP = 4;
//
//    private int NOTIFICATION_ID_CUSTOM_VIEW = 5;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notification);
//
//        setTitle("dev2qa.com --- Android Notification Example");
//
//        this.sendNormalSizeNotification();
//
//        this.sendLargeSizeNotification();
//
//        this.sendProgressBarNotification();
//
//        this.sendHeadsUpNotification();
//
//        this.sendCustomViewNotification();
//
//        this.deleteProgressBarNotification();
//
//        this.deleteAllNotification();
//    }
//
//    // Click a button to show a normal screen size notification.
//    private void sendNormalSizeNotification() {
//        Button sendNormalSizeNotificationButton = (Button) findViewById(R.id.sendNormalSizeNotificationButton);
//        sendNormalSizeNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Create NotificationManager.
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                // NotificationTargetActivity is the activity opened when user click notification.
//                Intent intent = new Intent(NotificationActivity.this, NotificationTargetActivity.class);
//                Intent intentArr[] = {intent};
//
//                PendingIntent pendingIntent = PendingIntent.getActivities(NotificationActivity.this, 0, intentArr, 0);
//
//                // Create a Notification Builder instance.
//                String title = "Normal Size Happy Christmas. ";
//                String textContent = "Christmas is comming --- dev2qa.com";
//                int smallIconResId = R.drawable.message_block;
//                int largeIconResId = R.drawable.if_candy_cane;
//                long sendTime = System.currentTimeMillis();
//                boolean autoCancel = false;
//
//                // Get general settings Builder instance.
//                NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, smallIconResId, largeIconResId, autoCancel, sendTime);
//
//                // Set content intent.
//                builder.setContentIntent(pendingIntent);
//
//                // Use both light, sound and vibrate.
//                builder.setDefaults(Notification.DEFAULT_ALL);
//
//                // Create Notification instance.
//                Notification notification = builder.build();
//                notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_INSISTENT;
//
//                // Send the notification.
//                notificationManager.notify(NOTIFICATION_ID_NORMAL_SIZE, notification);
//            }
//        });
//    }
//
//    // Click button to show a large screen size notification.
//    private void sendLargeSizeNotification() {
//        Button sendLargeSizeNotificationButton = (Button) findViewById(R.id.sendLargeSizeNotificationButton);
//        sendLargeSizeNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Create NotificationManager.
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                // NotificationTargetActivity is the activity opened when user click notification.
//                Intent intent = new Intent(NotificationActivity.this, NotificationTargetActivity.class);
//                Intent intentArr[] = {intent};
//
//                PendingIntent pendingIntent = PendingIntent.getActivities(NotificationActivity.this, 0, intentArr, 0);
//
//                // Create a Notification Builder instance.
//                String title = "Large Size Happy Christmas. ";
//                String textContent = "Christmas is comming --- dev2qa.com";
//                int smallIconResId = R.drawable.message_bookmark;
//                int largeIconResId = R.drawable.if_present;
//                long sendTime = System.currentTimeMillis();
//                boolean autoCancel = false;
//
//                // Get general settings Builder instance.
//                NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, smallIconResId, largeIconResId, autoCancel, sendTime);
//
//                // Create a BigTextStyle object.
//                NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
//                bigTextStyle.bigText("Hello, this is the large screen size notification example");
//                bigTextStyle.setBigContentTitle("Happy Christmas Detail Info.");
//                // Set big text style.
//                builder.setStyle(bigTextStyle);
//
//                // Set content intent.
//                builder.setContentIntent(pendingIntent);
//
//                // Use both light, sound and vibrate.
//                builder.setDefaults(Notification.DEFAULT_ALL);
//
//                // Create Notification instance.
//                Notification notification = builder.build();
//                notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_INSISTENT;
//
//                // Send the notification.
//                notificationManager.notify(NOTIFICATION_ID_LARGE_SIZE, notification);
//            }
//        });
//    }
//
//    // Click button to show a progress bar notification.
//    private void sendProgressBarNotification() {
//        Button sendProgressBarNotificationButton = (Button) findViewById(R.id.sendProgressBarNotificationButton);
//        sendProgressBarNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Create NotificationManager.
//                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                // NotificationTargetActivity is the activity opened when user click notification.
//                Intent intent = new Intent(NotificationActivity.this, NotificationTargetActivity.class);
//                Intent intentArr[] = {intent};
//
//                PendingIntent pendingIntent = PendingIntent.getActivities(NotificationActivity.this, 0, intentArr, 0);
//
//                // Create a Notification Builder instance.
//                String title = "Movie Download. ";
//                String textContent = "Download progress...";
//                int smallIconResId = R.drawable.message_delete;
//                int largeIconResId = R.drawable.if_reindeer;
//                long sendTime = System.currentTimeMillis();
//                boolean autoCancel = false;
//
//                // Get general settings Builder instance.
//                final NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, smallIconResId, largeIconResId, autoCancel, sendTime);
//
//                // Set content intent.
//                builder.setContentIntent(pendingIntent);
//
//                // Use both light, sound and vibrate.
//                builder.setDefaults(Notification.DEFAULT_ALL);
//
//                // The thread object will update the Notification progress programmatically.
//                Thread updateProgressThread = new Thread() {
//                    @Override
//                    public void run() {
//                        // Update the progress bar each second.
//                        for (int i = 0; i <= 10; i++) {
//                            //.builder.setProgress(10, i, true) will show a indeterminate progress bar.
//                            builder.setProgress(10, i, false);
//                            // Create Notification instance.
//                            final Notification notification = builder.build();
//                            notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//
//                            notificationManager.notify(NOTIFICATION_ID_PROGRESS_BAR, notification);
//                            try {
//                                // Sleep for 1 seconds
//                                Thread.sleep(1 * 1000);
//                            } catch (InterruptedException ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                        builder.setContentText("Download complete");
//                        //.builder.setProgress(0, 0, true) will show a indeterminate progress bar.
//                        builder.setProgress(0, 0, false);
//                        // Send the notification.
//                        // Create Notification instance.
//                        final Notification notification = builder.build();
//                        notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
//                        notificationManager.notify(NOTIFICATION_ID_PROGRESS_BAR, notification);
//                    }
//                };
//
//                // Start the thread.
//                updateProgressThread.start();
//            }
//        });
//    }
//
//    // Click button to show a Heads-up notification.
//    private void sendHeadsUpNotification() {
//        Button sendHeadsUpNotificationButton = (Button) findViewById(R.id.sendHeadsUpNotificationButton);
//        sendHeadsUpNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Create NotificationManager
//                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                // NotificationTargetActivity is the activity opened when user click notification.
//                Intent intent = new Intent(NotificationActivity.this, NotificationTargetActivity.class);
//                Intent intentArr[] = {intent};
//
//                PendingIntent pendingIntent = PendingIntent.getActivities(NotificationActivity.this, 0, intentArr, 0);
//
//                // Create a Notification Builder instance.
//                String title = "Happy Christmas Heads Up!!!. ";
//                String textContent = "Jingle bells, jingle bells Jingle all the way Oh what fun it is to ride in a one horse open sleigh, hey.";
//                int smallIconResId = R.drawable.message_new;
//                int largeIconResId = R.drawable.if_santa;
//                long sendTime = System.currentTimeMillis();
//                boolean autoCancel = false;
//
//                // Get general settings Builder instance.
//                final NotificationCompat.Builder builder = getGeneralNotificationBuilder(title, textContent, smallIconResId, largeIconResId, autoCancel, sendTime);
//
//                // Use both light, sound and vibrate.
//                builder.setDefaults(Notification.DEFAULT_ALL);
//
//                // Set a heads-up notification.
//                builder.setFullScreenIntent(pendingIntent, true);
//
//                // Create Notification instance.
//                Notification notification = builder.build();
//                notificationManager.notify(NOTIFICATION_ID_HEADS_UP, notification);
//            }
//        });
//    }
//
//    // Click button to show a custom view notification.
//    private void sendCustomViewNotification() {
//        Button sendCustomViewNotificationButton = (Button) findViewById(R.id.sendCustomViewNotificationButton);
//        sendCustomViewNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // Create NotificationManager
//                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                // NotificationTargetActivity is the activity opened when user click notification.
//                Intent intent = new Intent(NotificationActivity.this, NotificationTargetActivity.class);
//                Intent intentArr[] = {intent};
//
//                PendingIntent pendingIntent = PendingIntent.getActivities(NotificationActivity.this, 0, intentArr, 0);
//
//                // Create a new Notification instance.
//                Notification notification = new Notification();
//
//                // Set small icon.
//                notification.icon = R.drawable.message_settings;
//
//                // Set large icon.
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable(R.drawable.if_snowflake);
//                Bitmap largeIconBitmap = bitmapDrawable.getBitmap();
//                notification.largeIcon = largeIconBitmap;
//
//                // Set flags.
//                notification.flags = Notification.FLAG_ONGOING_EVENT;
//
//                // Set send time.
//                notification.when = System.currentTimeMillis();
//
//                // Create and set notification content view.
//                RemoteViews customRemoteViews = new RemoteViews(getPackageName(), R.layout.activity_notification_custom_view);
//                notification.contentView = customRemoteViews;
//
//                // Set notification intent.
//                notification.contentIntent = pendingIntent;
//
//                notificationManager.notify(NOTIFICATION_ID_CUSTOM_VIEW, notification);
//            }
//        });
//    }
//
//    // This method create and return a general Notification Builder instance.
//    private NotificationCompat.Builder getGeneralNotificationBuilder(String title, String textContent, int smallIconResId, int largeIconResId, boolean autoCancel, long sendTime) {
//        // Create a Notification Builder instance.
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationActivity.this);
//
//        // Set small icon.
//        builder.setSmallIcon(smallIconResId);
//
//        // Set large icon.
//        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable(largeIconResId);
//        Bitmap largeIconBitmap = bitmapDrawable.getBitmap();
//        builder.setLargeIcon(largeIconBitmap);
//
//        // Set title.
//        builder.setContentTitle(title);
//
//        // Set content text.
//        builder.setContentText(textContent);
//
//        // Set notification send time.
//        builder.setWhen(sendTime);
//
//        // If true then cancel the notification automatically.
//        builder.setAutoCancel(autoCancel);
//
//        return builder;
//    }
//
//    // Delete the ProgressBar notification.
//    private void deleteProgressBarNotification() {
//        Button deleteProgressBarNotificationButton = (Button) findViewById(R.id.deleteProgressBarNotificationButton);
//        deleteProgressBarNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Create NotificationManager
//                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancel(NOTIFICATION_ID_PROGRESS_BAR);
//            }
//        });
//    }
//
//    // Delete all notification.
//    private void deleteAllNotification() {
//        Button deleteAllNotificationButton = (Button) findViewById(R.id.deleteAllNotificationButton);
//        deleteAllNotificationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Create NotificationManager
//                final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.cancelAll();
//            }
//        });
//    }
//}
