package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

public class DisplayStudentProfile extends AppCompatActivity {

    TextView nameEditText, matricEditText, phoneEditText, departmentEditText, emailEditText, chapelEditText, demeritTextView;
    private ImageView imageView;
    String matric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_student_profile);

        Intent intent = getIntent();
        matric = intent.getStringExtra("matric");

        demeritTextView = findViewById(R.id.demeritTextView);
        imageView = findViewById(R.id.imageView);
        nameEditText = findViewById(R.id.nameEditText);
        matricEditText = findViewById(R.id.matricEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        chapelEditText = findViewById(R.id.chapelEditText);
        emailEditText = findViewById(R.id.emailTextView);

        //demeritTextView.setText(RequestReaderActivity.demeritTextView.getText().toString());

        final ArrayList<String> demeritList = new ArrayList<>();

        ParseQuery<ParseObject> query3 = new ParseQuery<ParseObject>("Attendance");
        query3.whereNotEqualTo("availableStudent", matric);
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

                        demeritTextView.setText("0 Point demerit ");
                    }
                } else {
                    MYToast.makeToast(DisplayStudentProfile.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", matric);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {

                        matricEditText.setText(object.get("username").toString().trim());
                        emailEditText.setText(object.get("emails").toString().trim());
                        nameEditText.setText(object.get("name").toString().trim());
                        chapelEditText.setText(object.get("chapel").toString().trim());
                        departmentEditText.setText(object.get("department").toString().trim());
                        phoneEditText.setText(object.get("phone").toString().trim());

                        ParseFile file = (ParseFile) object.get("displayImage");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (data != null && e == null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0 , data.length);
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    MYToast.makeToast(DisplayStudentProfile.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                        });
                    }

                }
            }
        });
    }

    public void DemeritClicked(View view) {
        Intent intent = new Intent(DisplayStudentProfile.this, AbsentActivity.class);
        intent.putExtra("matric", matric);
        startActivity(intent);
    }
}