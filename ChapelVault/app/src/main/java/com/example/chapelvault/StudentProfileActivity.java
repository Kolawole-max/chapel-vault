package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

public class StudentProfileActivity extends AppCompatActivity {

    ArrayList<String> absentList = new ArrayList<>();

    private TextView nameEditText, matricEditText, phoneEditText, departmentEditText, emailEditText, chapelEditText, demeritTextView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        demeritTextView = findViewById(R.id.demeritTextView);
        imageView = findViewById(R.id.imageView);
        nameEditText = findViewById(R.id.nameEditText);
        matricEditText = findViewById(R.id.matricEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        emailEditText = findViewById(R.id.emailEditText);
        chapelEditText = findViewById(R.id.chapelEditText);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {

                        matricEditText.setText(object.get("username").toString());
                        emailEditText.setText(object.get("email").toString());
                        nameEditText.setText(object.get("name").toString());
                        chapelEditText.setText(object.get("chapel").toString());
                        departmentEditText.setText(object.get("department").toString());
                        phoneEditText.setText(object.get("phone").toString());

                        ParseFile file = (ParseFile) object.get("displayImage");

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (data != null && e == null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0 , data.length);
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    Log.i("Profile log", e.getMessage());
                                    MYToast.makeToast(StudentProfileActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                        });
                    }
                }
            }
        });

        absentList.clear();
        ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Attendance");
        query1.whereNotEqualTo("availableStudent", ParseUser.getCurrentUser().getUsername());
        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {

                        for (ParseObject object : objects) {
                            absentList.add(object.getString("date"));
                        }
                        int daysAbsent = absentList.size() * 10;
                        demeritTextView.setText(daysAbsent + " POINTS DEMERIT");

                    } else {
                        demeritTextView.setText("0 POINT DEMERIT");
                    }

                } else {
                    Log.i("Demerit log", e.getMessage());
                    MYToast.makeToast(StudentProfileActivity.this, "Something went wrong!",  MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });
    }

    public void UpdateProfileClicked(View view) {

        Intent intent = new Intent(StudentProfileActivity.this, UpdateProfileActivity.class);
        startActivity(intent);
    }

    public void DemeritClicked(View view) {

        Intent intent = new Intent(StudentProfileActivity.this, AbsentActivity.class);
        intent.putExtra("matric", ParseUser.getCurrentUser().getUsername());
        startActivity(intent);
    }
}