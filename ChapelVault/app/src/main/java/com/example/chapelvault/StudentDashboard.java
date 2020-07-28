package com.example.chapelvault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class StudentDashboard extends AppCompatActivity {

    ParseUser user;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Dashboard");
        toolbar.setTitleTextColor(Color.BLACK);

        user = new ParseUser();

    }

    public void QrCodeClicked(View view) {
        Intent intent = new Intent(StudentDashboard.this, StudentQrCodeActivity.class);
        startActivity(intent);
    }

    public void ProfileClicked(View view) {
        Intent intent = new Intent(StudentDashboard.this, StudentProfileActivity.class);
        startActivity(intent);
    }

    public void logoutClicked(View view) {

        new AlertDialog.Builder(StudentDashboard.this)
                .setTitle("Are you sure?")
                .setMessage("Do you want to logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Intent intent = new Intent(StudentDashboard.this, LoginActivity.class);
                                    startActivity(intent);
                                    MYToast.makeToast(StudentDashboard.this, "Log out successful", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }

    public void AboutUsClicked(View view) {
        Intent intent = new Intent(StudentDashboard.this, AboutActivty.class);
        startActivity(intent);
    }

    public void RequestClicked(View view) {
        Intent intent = new Intent(StudentDashboard.this, RequestLeave.class);
        startActivity(intent);
    }
}