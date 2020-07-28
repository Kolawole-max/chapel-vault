package com.example.chapelvault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class StudentQrCodeActivity extends AppCompatActivity {

    ImageView imageView;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qr_code);

        user = new ParseUser();
        imageView = findViewById(R.id.imageView);

        ParseQuery<ParseObject> query = new ParseQuery<>("Image");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        ParseFile file = (ParseFile) object.get("qrCode");

                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (data != null && e == null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0 , data.length);
                                    imageView.setImageBitmap(bitmap);
                                } else {
                                    MYToast.makeToast(StudentQrCodeActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void BackImageClicked(View view) {
        onBackPressed();
    }

    public void ChaplainTextClicked(View view) {
        Intent intent = new Intent(StudentQrCodeActivity.this, DisplayAdminProfile.class);
        startActivity(intent);
    }
}