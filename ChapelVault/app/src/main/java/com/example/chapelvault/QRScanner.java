package com.example.chapelvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class QRScanner extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView scannView;

    String dateIntent;
    String objectIdIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);

        Intent intent = getIntent();
        dateIntent = intent.getStringExtra("date");
        objectIdIntent = intent.getStringExtra("objectId");

        scannView = findViewById(R.id.scannerView);
        codeScanner = new CodeScanner(this, scannView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Result from qr code
                        AdminAppointmentActivity.availableStudent.add(result.getText());
                        AdminAppointmentActivity.adapter.notifyDataSetChanged();

                        AdminAppointmentActivity.linearLayout.setVisibility(View.GONE);
                        AdminAppointmentActivity.listView.setVisibility(View.VISIBLE);

                        ParseQuery<ParseObject> query = new ParseQuery<>("Attendance");
                        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        query.whereEqualTo("date", dateIntent);
                        query.whereEqualTo("objectId", objectIdIntent);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        for (ParseObject object : objects) {

                                            object.put("availableStudent", AdminAppointmentActivity.availableStudent);
                                            object.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e == null) {
                                                        MYToast.makeToast(QRScanner.this, "Upload successful", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();
                                                    } else {
                                                        MYToast.makeToast(QRScanner.this, "Upload unsuccessful",MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    }
                });

            }
        });

        scannView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();

    }

    public void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(QRScanner.this, "Camera Permission is Required.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).check();
    }
}