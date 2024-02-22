package com.praise.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//this uses the joke api to get jokes and also set an alarm in which user recieves a joke notification
public class Display_joke extends AppCompatActivity {
    TextView textCat, txtJoke, txtres;
    Button button;
    DrawerLayout drawerLayout;
    private static final String CHANNEL_ID = "channel_ID";
    AlarmManager manager;
    PendingIntent pendingIntent;
    private Calendar calendar;

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);


        }

    }

    public static void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Log out");
        builder.setMessage("Are you sure you want to Exit the app ?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
                System.exit(0);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_joke);
        drawerLayout = findViewById(R.id.drawer_layout);
        textCat = findViewById(R.id.category);
        txtJoke = findViewById(R.id.question);
        txtres = findViewById(R.id.answer);
        button = findViewById(R.id.getNew);

        fetchData();
        setAlarm();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
    }

    private void fetchData() {
        String url = "https://v2.jokeapi.dev/joke/Any";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Display_joke.this, "error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    Display_joke.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                String type = jsonObject.getString("type");
                                if (type.equals("twopart")) {
                                    String category = jsonObject.getString("category");
                                    String setup = jsonObject.getString("setup");
                                    String delivery = jsonObject.getString("delivery");
                                    textCat.setText(category);
                                    txtJoke.setText(setup);
                                    txtres.setVisibility(View.VISIBLE);
                                    txtres.setText(delivery);
                                } else {
                                    String joke = jsonObject.getString("joke");
                                    String category = jsonObject.getString("category");
                                    txtJoke.setText(joke);
                                    txtres.setVisibility(View.INVISIBLE);
                                    textCat.setText(category);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);

    }

    public void ClickHome(View view) {
        redirectActivity(this, MainActivity.class);
    }

    public void ClickJoke(View view) {
        closeDrawer(drawerLayout);
    }

    public void ClickTrivia(View view) {
        redirectActivity(this, Trivia.class);
    }

    public void ClickLogout(View view) {
        logout(this);
    }

    @Override
    public void onBackPressed() {
        return;
    }
    private void setAlarm() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        createNotificationChannel();
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReciever.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 10, pendingIntent);
        Toast.makeText(Display_joke.this, "Alarm has been set", Toast.LENGTH_SHORT).show();

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "timeAlarm";
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}