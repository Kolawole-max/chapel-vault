package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamed.notificationbar.NotificationBar;
import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

public class RequestLeave extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    String currentDateString;

    private TextView  chaplainTextView, nameTextView, datePickTextView;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_leave);

        editText = findViewById(R.id.editText);
        datePickTextView = findViewById(R.id.pickDateTextView);
        chaplainTextView = findViewById(R.id.chaplainTextView);
        nameTextView = findViewById(R.id.nameTextView);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {

                            nameTextView.setText(object.getString("name"));
                        }
                    } else {
                        nameTextView.setText("NULL");
                    }
                }
            }
        });

        ParseQuery<ParseUser> query1 = ParseUser.getQuery();
        query1.whereEqualTo("userType", "Chaplain");
        query1.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (ParseObject object : objects) {

                            chaplainTextView.setText(object.getString("name"));
                        }
                    } else {
                        chaplainTextView.setText("NULL");
                    }
                }
            }
        });

        datePickTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");

            }
        });

    }

    public void sendRequestClicked(View view) {

        ParseObject object = new ParseObject("Request");
        object.put("student", ParseUser.getCurrentUser().getUsername());
        object.put("message", editText.getText().toString());
        object.put("date", currentDateString);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    editText.setText("");
                    datePickTextView.setText("Pick a date");

                    NotificationBar.create(RequestLeave.this)
                            .setTitle("Request has been sent successfully.")
                            .setMessage("Notification")
                            .setIcon(R.drawable.adelekelogo)
                            .setBackgroundColor(R.color.colorPrimary)
                            .setDuration(5000)
                            .setAnimationIn(android.R.anim.fade_in, android.R.anim.fade_in)
                            .setAnimationOut(android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                            .setNotificationPosition(NotificationBar.BOTTOM)
                            .show();

                } else {
                    MYToast.makeToast(RequestLeave.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        datePickTextView.setText(currentDateString);
    }

    public void BackImageClicked(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}