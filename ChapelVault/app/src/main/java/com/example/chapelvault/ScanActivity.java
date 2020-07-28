package com.example.chapelvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ScanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    ParseUser user;
    Button button;
    String dateSelected;

    MyExpandableListAdapter adapter;
    ExpandableListView expandableListView;

    List<String> date = new ArrayList<>();
    List<String> student = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Scan Activity");

        expandableListView = findViewById(R.id.expandableListView);
        user = new ParseUser();

        final HashMap<String, List<String>> item = new HashMap<>();
        student.add("Scan QR code");

        item.put("Testing", student);
        expandableListView.setAdapter(adapter);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Attendance");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            date.add(objects.get(i).getString("date"));

                            JSONArray array = objects.get(i).getJSONArray("availableStudent");

                            for(int j = 0 ; j < array.length(); j++) {
                                try {
                                    student.add(array.getString(j));
                                    //item.put(date.get(i), student);
                                } catch (Exception e1) {
                                    MYToast.makeToast(ScanActivity.this, e1.getMessage(), MYToast.CUSTOM_GRAVITY_BOTTOM, MYToast.CUSTOM_TYPE_ERROR, MYToast.LENGTH_LONG).show();
                                }
                            }
                            //expandableListView.setAdapter(adapter);
                        }
                        expandableListView.setAdapter(adapter);
                    }
                }
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(ScanActivity.this, "Group Position " + groupPosition, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        dateSelected = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        button.setText(dateSelected);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scanmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {

        if (item.getItemId() == R.id.newWeek) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ScanActivity.this);

            dialog.setTitle("Add new week");

            button = new Button(ScanActivity.this);
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

                    ArrayList<String> windowsGroups = new ArrayList<>();
                    windowsGroups.add("17/001");
                    windowsGroups.add("17/002");

                    ParseObject object = new ParseObject("Attendance");
                    object.put("username", ParseUser.getCurrentUser().getUsername());
                    object.put("date", dateSelected);
                    object.put("availableStudent", windowsGroups);

                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(ScanActivity.this, "New week has been added successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(ScanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            dialog.setNegativeButton("Cancel", null);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
}