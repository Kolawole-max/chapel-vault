package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;

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

public class DisplayAdminProfile extends AppCompatActivity {

    private TextView nameEditText, matricEditText, phoneEditText, departmentEditText, emailEditText, chapelEditText;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_admin_profile);

        imageView = findViewById(R.id.imageView);
        nameEditText = findViewById(R.id.nameEditText);
        matricEditText = findViewById(R.id.matricEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        emailEditText = findViewById(R.id.emailTextView);
        chapelEditText = findViewById(R.id.chapelEditText);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("userType", "Chaplain");
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
                                    MYToast.makeToast(DisplayAdminProfile.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                        });
                    }

                } else {
                    MYToast.makeToast(DisplayAdminProfile.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });
    }

    public void BackImageClicked(View view) {
        onBackPressed();
    }
}