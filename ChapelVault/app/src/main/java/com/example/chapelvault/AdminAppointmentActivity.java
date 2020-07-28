package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AdminAppointmentActivity extends AppCompatActivity {

    static ArrayAdapter<String> adapter;
    static ArrayList<String> availableStudent = new ArrayList<>();

    TextView notifyTextView;
    ImageView backImageView;
    static LinearLayout linearLayout;
    static ListView listView;

    String dateIntent;
    String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_appointment);

        final Intent intent = getIntent();
        dateIntent = intent.getStringExtra("date");
        objectId = intent.getStringExtra("objectId");

        notifyTextView = findViewById(R.id.notifyTextView);
        backImageView = findViewById(R.id.backImageView);
        listView = findViewById(R.id.listView);
        linearLayout = findViewById(R.id.linearLayout);

        notifyTextView.setText("Students scanned for " + dateIntent);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, availableStudent);
        listView.setAdapter(adapter);

        ParseQuery<ParseObject> query = new ParseQuery<>("Attendance");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("date", dateIntent);
        query.whereEqualTo("objectId", objectId);

        availableStudent.clear();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {

                            JSONArray array = objects.get(i).getJSONArray("availableStudent");

                            for (int j = 0; j < array.length(); j++) {
                                try {

                                    availableStudent.add(array.getString(j));
                                } catch (Exception e1) {
                                    MYToast.makeToast(AdminAppointmentActivity.this, e1.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                            if (availableStudent.size() > 0) {

                                listView.setVisibility(View.VISIBLE);
                                linearLayout.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();

                            } else {

                                listView.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        listView.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    MYToast.makeToast(AdminAppointmentActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent1 = new Intent(AdminAppointmentActivity.this, DisplayStudentProfile.class);
                intent1.putExtra("matric", availableStudent.get(i));
                startActivity(intent1);
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
        //Scan student qr code
        Intent intent = new Intent(AdminAppointmentActivity.this, QRScanner.class);
        intent.putExtra("date", dateIntent);
        intent.putExtra("objectId", objectId);
        startActivity(intent);
    }
}