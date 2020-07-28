package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.mohamed.notificationbar.NotificationBar;
import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RequestReaderActivity extends AppCompatActivity {

    ArrayList<String> demeritList = new ArrayList<>();
    static TextView demeritTextView;

    String objectIdIntent;

    TextView matricTextView, dateTextView, messageTextView, nameTextView, statusTextView;
    MaterialButton approveButton, declineButton;
    ImageView backImageView, profileImageView;
    CardView declineCardView, approveCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_reader);

        Intent intent = getIntent();

        final String matric = intent.getStringExtra("unread");
        final String readMatric = intent.getStringExtra("read");
        objectIdIntent = intent.getStringExtra("objectId");

        statusTextView = findViewById(R.id.statusTextView);
        matricTextView = findViewById(R.id.matricTextView);
        demeritTextView = findViewById(R.id.demeritTextView);
        dateTextView = findViewById(R.id.dateTextView);
        messageTextView = findViewById(R.id.messageTextView);
        nameTextView = findViewById(R.id.nameTextView);

        backImageView = findViewById(R.id.backImageView);
        profileImageView = findViewById(R.id.profileImageView);

        approveButton = findViewById(R.id.approveButton);
        declineButton = findViewById(R.id.declineButton);

        approveCardView = findViewById(R.id.approveCardView);
        declineCardView = findViewById(R.id.declineCardView);

        if (readMatric == null) {

            LoadMessage(matric);
            statusTextView.setVisibility(View.GONE);
            approveCardView.setVisibility(View.VISIBLE);
            declineCardView.setVisibility(View.VISIBLE);
            approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Status("Approved", matric, objectIdIntent);
                }
            });

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Status("Declined", matric, objectIdIntent);
                }
            });

        } else {

            statusTextView.setVisibility(View.VISIBLE);
            approveCardView.setVisibility(View.INVISIBLE);
            declineCardView.setVisibility(View.INVISIBLE);
            LoadMessage(readMatric);
        }

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void LoadMessage(final String matricNumber) {

        ParseQuery<ParseUser> query1 = ParseUser.getQuery();
        query1.whereEqualTo("username", matricNumber);
        query1.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {
                            matricTextView.setText(object.getString("username").trim());
                            nameTextView.setText(object.getString("name").trim());
                        }
                    } else {
                        matricTextView.setText("Null");
                        nameTextView.setText("Null");
                    }
                } else {
                    MYToast.makeToast(RequestReaderActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });

        ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("Attendance");
        query3.whereNotEqualTo("availableStudent", matricNumber);
        query3.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {
                            demeritList.add(object.getString("date"));
                        }
                        int demerit = demeritList.size() * 10;
                        demeritTextView.setText(demerit + " Points demerit ");

                    } else {

                        demeritList.add("No record found");
                        demeritTextView.setText("0 Point demerit ");
                    }
                } else {
                    MYToast.makeToast(RequestReaderActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });

        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Request");
        query.whereEqualTo("student", matricNumber);
        query.whereEqualTo("objectId", objectIdIntent);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {
                            messageTextView.setText(object.getString("message").trim());
                            dateTextView.setText(object.getString("date").trim());

                            if (object.getString("status") != null) {
                                statusTextView.setText("Status: " + object.getString("status").toUpperCase());
                            }
                        }
                    } else {
                        messageTextView.setText("Null");
                        dateTextView.setText("Null");
                    }
                } else {
                    MYToast.makeToast(RequestReaderActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Student Profile...
                Intent intent1 = new Intent(RequestReaderActivity.this, DisplayStudentProfile.class);
                intent1.putExtra("matric", matricNumber);
                startActivity(intent1);
            }
        });

    }

    public void Status(final String status, final String matricNo, final String objectId) {

        ParseQuery<ParseObject> query4 = new ParseQuery<ParseObject>("Request");
        query4.whereEqualTo("student", matricNo);
        query4.whereEqualTo("objectId", objectId);
        query4.whereDoesNotExist("status");
        query4.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0 ) {
                        for (ParseObject object : objects ) {

                            object.put("status", status);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                        ReadFragment.linearLayout.setVisibility(View.GONE);
                                        ReadFragment.listView.setVisibility(View.VISIBLE);
                                        ReadFragment.read.add(matricNo);
                                        ReadFragment.objectId.add(objectId);
                                        ReadFragment.arrayAdapter.notifyDataSetChanged();

                                        //Edit Toast message to Notification

                                        UnreadFragment.unread.remove(matricNo);
                                        if (UnreadFragment.unread.size() < 1){
                                            UnreadFragment.linearLayout.setVisibility(View.VISIBLE);
                                            UnreadFragment.listView.setVisibility(View.GONE);
                                        }
                                        UnreadFragment.arrayAdapter.notifyDataSetChanged();

                                        declineButton.setEnabled(false);
                                        approveButton.setEnabled(false);

                                        //onBackPressed();

                                        NotificationBar.create(RequestReaderActivity.this)
                                                .setTitle("Request " + status + " successfully.")
                                                .setMessage("Notification")
                                                .setIcon(R.drawable.adelekelogo)
                                                .setBackgroundColor(R.color.colorPrimary)
                                                .setDuration(5000)
                                                .setAnimationIn(android.R.anim.fade_in, android.R.anim.fade_in)
                                                .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                                                .setNotificationPosition(NotificationBar.BOTTOM)
                                                .show();

                                    } else {
                                        MYToast.makeToast(RequestReaderActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    MYToast.makeToast(RequestReaderActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });
    }
}