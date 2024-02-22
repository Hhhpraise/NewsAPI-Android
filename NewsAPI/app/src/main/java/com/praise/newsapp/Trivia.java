package com.praise.newsapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Trivia extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView textCat, txtJoke, txtres;
    Button button;

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
        setContentView(R.layout.activity_trivia);
        drawerLayout = findViewById(R.id.drawer_layout);
        textCat = findViewById(R.id.category);
        txtJoke = findViewById(R.id.question);
        txtres = findViewById(R.id.answer);
        button = findViewById(R.id.getNew);
        fetchData();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });

    }

    private void fetchData() {
        String url = "https://jservice.io/api/random";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(Trivia.this, "error " +e, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    Trivia.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(resp);
                                String quest = jsonArray.getString(0);
                                JSONObject jsonObject = new JSONObject(quest);
                                    String question = jsonObject.getString("question");
                                    String answer = jsonObject.getString("answer");
                                    txtJoke.setText("Question :"+question);
                                    txtres.setText("Answer : "+answer);
                                    String category = jsonObject.getString("category");
                                    JSONObject object = new JSONObject(category);
                                    String cat = object.getString("title");
                                textCat.setText(cat);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Trivia.this, "error " +e, Toast.LENGTH_SHORT).show();
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
        redirectActivity(this, Display_joke.class);
    }

    public void ClickTrivia(View view) {
        closeDrawer(drawerLayout);
    }

    public void ClickLogout(View view) {
        logout(this);
    }

    @Override
    public void onBackPressed() {
        return;
    }
}