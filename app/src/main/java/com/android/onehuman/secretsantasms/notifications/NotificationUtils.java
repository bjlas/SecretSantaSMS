package com.android.onehuman.secretsantasms.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.onehuman.secretsantasms.GroupsList;
import com.android.onehuman.secretsantasms.R;

public class NotificationUtils {

    private Context context;
    private static final String CHANNEL_ID = "channel_secretsantasms";
    public static final int NOTIFICATION_ID = 1;

    public NotificationUtils(Context c) {
        this.context=c;
    }

    public void showNotification(String title, String text) {
        createNotificationChannel();

        RemoteViews remoteCollapsedViews = new RemoteViews(context.getPackageName(), R.layout.notification_normal);

        Intent mainIntent = new Intent(context, GroupsList.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainPIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_candy)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(mainPIntent)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setCustomContentView(remoteCollapsedViews)
                ;


        



        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "notification";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
