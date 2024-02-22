package com.praise.newsapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlarmReciever extends BroadcastReceiver {
    private static final String CHANNEL_ID = "channel_ID";
    int notificationId = 2;
    String joke;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, Display_joke.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notify_icon)
                .setContentTitle("Jokes")
                .setContentText(joke)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(joke))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    private void fetchData() {
        String url = "https://v2.jokeapi.dev/joke/Any?type=single";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resp = response.body().string();


                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                         joke = jsonObject.getString("joke");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }

        });
    }
}

