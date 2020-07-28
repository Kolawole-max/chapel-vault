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

import java.util.List;

public class AdminProfile extends AppCompatActivity {

    TextView nameEditText, matricEditText, phoneEditText, departmentEditText, emailEditText, chapelEditText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        imageView = findViewById(R.id.imageView);
        nameEditText = findViewById(R.id.nameEditText);
        matricEditText = findViewById(R.id.matricEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        emailEditText = findViewById(R.id.emailTextView);
        chapelEditText = findViewById(R.id.chapelEditText);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
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
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                        imageView.setImageBitmap(bitmap);
                                    } else {
                                        MYToast.makeToast(AdminProfile.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                    }
                                }
                            });
                        }
                    }else {
                        MYToast.makeToast(AdminProfile.this, "Something went wrong", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                    }
                } else {
                    MYToast.makeToast(AdminProfile.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });
    }

    public void UpdateProfileClicked(View view) {
        Intent intent = new Intent(AdminProfile.this, UpdateProfileActivity.class);
        startActivity(intent);
    }
}