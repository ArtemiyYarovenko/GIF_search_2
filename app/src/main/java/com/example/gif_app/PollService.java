package com.example.gif_app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

import com.Object.API_Response;
import com.Object.Datum;
import com.example.gif_app.api.Retrofit_Caller;
import com.example.gif_app.api.Retrofit_Item;
import com.example.gif_app.main.Main;
import com.example.gif_app.main.RV_Adapter.Gif_Adapter;


import java.util.List;
import java.util.concurrent.TimeUnit;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);
    Retrofit r = Retrofit_Item.getRetrofit();

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            Log.i(TAG, "Нет соединения: " + intent);
            return;
        }
        Log.i(TAG, "Служба запущена ff: " + intent);
        Retrofit retrofit = Retrofit_Item.getRetrofit();
        retrofit.create(Retrofit_Caller.class)
                .getRecent("lkrJDzDTVXJtbt8lPVphAMKC05nGDLji", "50", "R")
                .enqueue(new Callback<API_Response>() {

                            @Override
                            public void onResponse(Call<API_Response> call, Response<API_Response> response) {
                                assert response.body() != null;
                                List<Datum> provided_values = response.body().getData();
                                Log.i(TAG, "Служба запущена ff: " + provided_values);
                            }

                            @Override
                            public void onFailure(Call<API_Response> call, Throwable t) {
                            }
                        });



    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = cm.getActiveNetworkInfo();
        return nwInfo != null && nwInfo.isConnected();

    }

}
