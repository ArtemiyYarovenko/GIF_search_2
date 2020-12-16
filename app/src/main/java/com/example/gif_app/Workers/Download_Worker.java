package com.example.gif_app.Workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.Object.API_Response;
import com.Object.Datum;
import com.Object.Images;
import com.example.gif_app.API.Retrofit_Caller;
import com.example.gif_app.API.Retrofit_Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Download_Worker extends Worker {
    public Download_Worker(
            @NonNull Context context,
            @NonNull WorkerParameters parameters) {
        super(context, parameters);
    }
    static final String TAG = "workmng";
    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor = sharedPreferences.edit();

    @Override
    public Result doWork() {
        Retrofit retrofit = Retrofit_Item.getRetrofit();
        retrofit.create(Retrofit_Caller.class)
                .getRecent("lkrJDzDTVXJtbt8lPVphAMKC05nGDLji", "50", "R")
                .enqueue(new Callback<API_Response>() {
                    @Override
                    public void onResponse(Call<API_Response> call, Response<API_Response> response) {
                        assert response.body() != null;
                        List<Datum> downloaded_gifs = response.body().getData();
                        Gson gson = new Gson();
                        List<Datum> saved;
                        Log.d(TAG, "Данные загружены в кол-ве " + downloaded_gifs.size());
                        String Alrdy_saved = sharedPreferences.getString("saved", "null");
                        if (!Alrdy_saved.equals("null")) {
                            saved = gson.fromJson(Alrdy_saved, new TypeToken<List<Datum>>(){}.getType());
                            saved.addAll(downloaded_gifs);
                            Log.d(TAG, "Кол-во элементов в хранилище" + saved.size());
                        } else {
                            saved = downloaded_gifs;
                            Log.d(TAG, "Кол-во элементов в хранилище" + saved.size());
                        }
                        String saver = gson.toJson(saved);
                        editor.putString("saved", saver);
                        editor.commit();




                    }

                    @Override
                    public void onFailure(Call<API_Response> call, Throwable t) {

                    }
                });
        return Result.success();
    }
}