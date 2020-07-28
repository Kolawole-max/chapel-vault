package com.example.chapelvault;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

public class UpdateProfileActivity extends AppCompatActivity {

    Button updateButton;
    ParseQuery<ParseUser> query;
    ParseFile file;
    Bitmap original;
    ParseUser user;
    private ImageView imageView;
    private EditText nameEditText, phoneEditText, chapelEditText, departmentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        //Parse.enableLocalDatastore(this);

        query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        user = new ParseUser();
        updateButton = findViewById(R.id.updateButton);
        nameEditText = findViewById(R.id.nameEditText);
        departmentEditText = findViewById(R.id.departmentEditText);
        chapelEditText = findViewById(R.id.chapelEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        imageView = findViewById(R.id.imageView);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {

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
                                    MYToast.makeToast(UpdateProfileActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void changeDisplayImage(View view) {
        new AlertDialog.Builder(UpdateProfileActivity.this)
                .setTitle("Are you sure?")
                .setMessage("Do you want to change your profile picture")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent to phone memory...
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                original = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                imageView.setImageBitmap(original);

                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> objects, ParseException e) {
                        if (e  == null && objects.size() > 0) {
                            for (ParseObject object : objects) {

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                original.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                file = new ParseFile("image.png", byteArray);
                                object.put("displayImage", file);

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            MYToast.makeToast(UpdateProfileActivity.this, "Image uploaded successful", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();                                        }
                                        else {
                                            MYToast.makeToast(UpdateProfileActivity.this, "Image upload failed", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();

                                        }
                                    }
                                });
                            }
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            MYToast.makeToast(UpdateProfileActivity.this, "Something went wrong", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void UpdateClicked(View view) {

        if (!validateChapel() | !validateDepartment() | !validateName() | !validatePhone()) {
            return;
        }

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects.size() > 0 & e == null) {
                    for (ParseObject object1 : objects) {

                        object1.put("name", nameEditText.getText().toString().trim());
                        object1.put("department", departmentEditText.getText().toString().trim());
                        object1.put("phone", phoneEditText.getText().toString().trim());
                        object1.put("chapel", chapelEditText.getText().toString().trim());

                        object1.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    MYToast.makeToast(UpdateProfileActivity.this, "Profile updated successfully", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();
                                    onBackPressed();
                                } else {
                                    MYToast.makeToast(UpdateProfileActivity.this, e.getMessage(), MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                                }
                            }
                        });
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void BackImageClicked(View view) {
        onBackPressed();
    }

    public Boolean validateName() {
        if (nameEditText.getText().toString().trim().matches("")) {
            nameEditText.setError("Field can't be empty");
            return false;
        } else {
            nameEditText.setError(null);
            return true;
        }
    }

    public Boolean validatePhone() {
        if (phoneEditText.getText().toString().trim().matches("")) {
            phoneEditText.setError("Field can't be empty");
            return false;
        } else {
            phoneEditText.setError(null);
            return true;
        }
    }

    public Boolean validateDepartment() {
        if (departmentEditText.getText().toString().trim().matches("")) {
            departmentEditText.setError("Field can't be empty");
            return false;
        } else {
            departmentEditText.setError(null);
            return true;
        }
    }

    public Boolean validateChapel() {
        if (chapelEditText.getText().toString().trim().matches("")) {
            chapelEditText.setError("Field can't be empty");
            return false;
        } else {
            chapelEditText.setError(null);
            return true;
        }
    }
}