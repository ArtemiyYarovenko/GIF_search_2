package com.example.gif_app.Main;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.Object.Datum;
import com.Object.API_Response;
import com.example.gif_app.Workers.Download_Worker;
import com.example.gif_app.R;
import com.example.gif_app.API.Retrofit_Item;
import com.example.gif_app.API.Retrofit_Caller;
import com.example.gif_app.DataBase.GIF_DB;
import com.example.gif_app.RV_Adapter.Gif_Adapter;
import com.example.gif_app.RV_Adapter.Gif_Adapter_2;
import com.example.gif_app.Workers.Notification_Worker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;



public class Main
        extends AppCompatActivity
        implements Gif_Adapter.OnInsertListener {

    private static final String api_key = "lkrJDzDTVXJtbt8lPVphAMKC05nGDLji";
    String limit = "50";
    String rating = "R";
    String offset = "0";
    int SpanCount = 2;
    Retrofit retrofit;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    public RecyclerView recycler_view;
    private GIF_DB DataBase;
    private EditText search_keyword;
    Button button_from_database;
    Button button_from_online;
    public Gif_Adapter gif_adapter;
    GridLayoutManager gridLayoutManager;
    List<Datum> provided_values;
    Type itemsListType = new TypeToken<List<Datum>>() {}.getType();
    Boolean IsLaunchFromNotification = false;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sharedPreferences = getSharedPreferences("default", 0);

       Intent intent = getIntent();
        if (intent != null) {
            IsLaunchFromNotification = intent.getBooleanExtra("Notify", false);
        }

        button_from_database = findViewById(R.id.load_local);
        button_from_online = findViewById(R.id.load_online);
        search_keyword = findViewById(R.id.search_input);
        recycler_view = findViewById(R.id.recycler_view_main);

        gridLayoutManager = new GridLayoutManager(this, SpanCount);
        gridLayoutManager.setItemPrefetchEnabled(true);
        gridLayoutManager.setInitialPrefetchItemCount(50);

        recycler_view.setLayoutManager(gridLayoutManager);

        DataBase = GIF_DB.getDatabase(this);

        retrofit = Retrofit_Item.getRetrofit();

        //возращение состояния ресайклер вью
        if(savedInstanceState !=null) {
            String string = savedInstanceState.getString("zip_data");
            Gson gson = new Gson();
            provided_values = gson.fromJson(string, itemsListType);

            Gif_Adapter gif_adapter = new Gif_Adapter(this, provided_values);
            recycler_view.setAdapter(gif_adapter);
        }

        if (IsLaunchFromNotification) {
            redraw();
        }


        View.OnClickListener click_button_load_db = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provided_values = DataBase.getGifDao().LoadAll();
                Gif_Adapter_2 recycler_view_adapter = new Gif_Adapter_2(this, provided_values);
                recycler_view.setAdapter(recycler_view_adapter);
            }
        };
        button_from_database.setOnClickListener(click_button_load_db);


        View.OnClickListener click_button_load_online = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = search_keyword.getText().toString();
                make_call(q);

            }
        };
        button_from_online.setOnClickListener(click_button_load_online);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Gson gson = new Gson();
        String string = gson.toJson(provided_values);
        savedInstanceState.putString("zip_data", string);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest Download_Req = new PeriodicWorkRequest.Builder
                (Download_Worker.class, 45, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
                .addTag("Repeat_Download")
                .setConstraints(constraints)
                .build();

        PeriodicWorkRequest Notify_Req = new PeriodicWorkRequest.Builder
                (Notification_Worker.class, 8, TimeUnit.HOURS)
//                .setInitialDelay(8,TimeUnit.HOURS)
                .addTag("Repeat_Notification")
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(Download_Req);
        WorkManager.getInstance(getApplicationContext()).enqueue(Notify_Req);

    }

    public void make_call(String q) {
        retrofit.create(Retrofit_Caller.class)
                .getSearchPhotos(api_key, q, limit, offset, rating)
                .enqueue(new Callback<API_Response>() {

                    @Override
                    public void onResponse(Call<API_Response> call, Response<API_Response> response) {
                        assert response.body() != null;
                        provided_values = response.body().getData();
                        gif_adapter = new Gif_Adapter(Main.this,provided_values);
                        gif_adapter.setOnInsertListener(Main.this);
                        recycler_view.setAdapter(gif_adapter);
                    }

                    @Override
                    public void onFailure(Call<API_Response> call, Throwable t) {
                    }
                });
    }

    // Вызывается, если активити запущена из нотификейшна, заполняя ресайклер вью теми картинками
    // которые были загружены Download_Workerом в фоновом режиме
    public void redraw() {
        Gson gson = new Gson();
        String cache = sharedPreferences.getString("saved", "");
        if (!cache.equals("")){
        List<Datum> saved = gson.fromJson(cache, new TypeToken<List<Datum>>(){}.getType());
        Collections.shuffle(saved);
        Gif_Adapter gif_adapter = new Gif_Adapter(this, saved);
        recycler_view.setAdapter(gif_adapter);}

    }

    @Override
    public void onInsert(Datum datum) {
        DataBase.getGifDao().insertGif(datum);
    }


}
