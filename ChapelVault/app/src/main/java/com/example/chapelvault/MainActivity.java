package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String userType;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.logoTextView);

        imageView.animate().alpha(1).setDuration(3000);
        textView.animate().alpha(1).setDuration(3000);

        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject object : objects) {
                            userType = object.get("userType").toString();
                        }
                    }
                }
            });
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ParseUser.getCurrentUser() != null) {
                    nextActivity();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 4000);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void nextActivity() {
        if (userType.matches("Student")) {
            Intent intent = new Intent(MainActivity.this, StudentDashboard.class);
            startActivity(intent);
        } else if (userType.matches("Chaplain")) {
            Intent intent1 = new Intent(MainActivity.this, AdminDashboard.class);
            startActivity(intent1);
        }
    }
}