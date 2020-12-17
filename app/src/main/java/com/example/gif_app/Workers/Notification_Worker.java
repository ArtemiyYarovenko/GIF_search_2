package com.example.gif_app.Workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.gif_app.Main.Main;



public class Notification_Worker extends Worker {
    public final String CHANNEL_ID = "166";
    public final CharSequence CHANNEL_NAME = "Takeit";
    public final int notificationId = 1;
    public Notification_Worker(
            @NonNull Context context,
            @NonNull WorkerParameters parameters) {
        super(context, parameters);
    }

    static final String TAG = "notify_mng";



    @Override
    public Result doWork() {
        createNofication();
        return Result.success();
    }

    public void createNofication(){
//intent to open our activity
        Intent intent = new Intent(getApplicationContext(), Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("Notify", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        NotificationChannel notificationChannel = new NotificationChannel
                (CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
//notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Привет от Gif_Search")
                .setContentText("У тебя накопились картинки, как насчет взглянуть на них?")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
//show notification
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(notificationId, builder.build());

        //notificationManagerCompat.notify(notificationId,builder.build());
    }


}