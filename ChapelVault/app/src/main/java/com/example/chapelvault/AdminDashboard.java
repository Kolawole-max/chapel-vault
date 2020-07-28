package com.example.chapelvault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mohamed.notificationbar.Interface.CustomViewInitializer;
import com.mohamed.notificationbar.Interface.OnActionClickListener;
import com.mohamed.notificationbar.NotificationBar;
import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class AdminDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
    }

    public void logoutClicked(View view) {
        new AlertDialog.Builder(AdminDashboard.this)
                .setTitle("Are you sure?")
                .setMessage("Do you want to logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.logOutInBackground(new LogOutCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                    Intent intent = new Intent(AdminDashboard.this, LoginActivity.class);
                                    startActivity(intent);
                                    MYToast.makeToast(AdminDashboard.this, "Log out successfully", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void AboutUsClicked(View view) {
        Intent intent = new Intent(AdminDashboard.this, AboutActivty.class);
        startActivity(intent);
    }

    public void ProfileClicked(View view) {
        Intent intent = new Intent(AdminDashboard.this, AdminProfile.class);
        startActivity(intent);
    }

    public void ScanClicked(View view) {
        Intent intent = new Intent(AdminDashboard.this, WeekAttendance.class);
        startActivity(intent);
    }

//    public void AppointmentClicked(View view) {
//        Intent intent = new Intent(AdminDashboard.this, ScanActivity.class);
//        startActivity(intent);
//    }

    public void ApproveRequest(View view) {
        Intent intent = new Intent(AdminDashboard.this, ApproveRequest.class);
        startActivity(intent);
    }
}