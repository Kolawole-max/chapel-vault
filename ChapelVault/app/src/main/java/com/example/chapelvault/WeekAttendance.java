package com.example.chapelvault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekAttendance extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    Button button;
    String dateSelected;

    ListView listView;
    ImageView backImageView;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> objectID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_attendance);

        backImageView = findViewById(R.id.backImageView);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Attendance");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() > 0 && e == null) {
                    for (ParseObject object : objects) {

                        arrayList.add(object.getString("date"));
                        objectID.add(object.getObjectId());
                    }
                    listView.setAdapter(adapter);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(WeekAttendance.this, AdminAppointmentActivity.class);
                intent.putExtra("date", arrayList.get(position));
                intent.putExtra("objectId", objectID.get(position));
                startActivity(intent);
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void FloatingClicked(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(WeekAttendance.this);

        dialog.setTitle("Add new week");

        button = new Button(WeekAttendance.this);
        button.setText("Pick a date");
        button.setTextSize(25);
        dialog.setView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ArrayList<String> emptyArray = new ArrayList<>();

                final ParseObject object = new ParseObject("Attendance");
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("date", dateSelected);
                object.put("availableStudent", emptyArray);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            MYToast.makeToast(WeekAttendance.this, "New week added successfully",
                                    MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();

                            arrayList.add(dateSelected);
                            adapter.notifyDataSetChanged();
                            objectID.add(object.getObjectId());

                        } else {
                            MYToast.makeToast(WeekAttendance.this, e.getMessage(), MYToast.LENGTH_LONG,
                                    MYToast.CUSTOM_TYPE_ERROR , MYToast.CUSTOM_GRAVITY_BOTTOM).show();

                        }
                    }
                });
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateSelected = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        button.setText(dateSelected);
    }
}