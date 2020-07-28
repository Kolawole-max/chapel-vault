package com.example.chapelvault;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    ParseUser user;
    private EditText matricEditText, emailEditText, passwordEditText;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        matricEditText = findViewById(R.id.matricEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        radioGroup = findViewById(R.id.radioGroup);

        user = new ParseUser();

        if (ParseUser.getCurrentUser() != null) {
            nextActivity();
        }
    }

    //Validate Radio button
    public Boolean validateUserType() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            MYToast.makeToast(SignUpActivity.this, "User type is required", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_WARNING, MYToast.CUSTOM_GRAVITY_CENTER).show();
            return false;
        } else {
            return true;
        }
    }

    public Boolean validateMatric() {
        if (matricEditText.getText().toString().trim().matches("")) {
            matricEditText.setError("Field cant be empty");
            return false;
        } else {
            matricEditText.setError(null);
            return true;
        }
    }
    public Boolean validateEmail() {
        if (emailEditText.getText().toString().trim().matches("")) {
            emailEditText.setError("Field cant be empty");
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }
    public Boolean validatePassword() {
        if (passwordEditText.getText().toString().trim().matches("")) {
            passwordEditText.setError("Field cant be empty");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    //Next Activity.
    public void nextActivity() {

        /*ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (objects.size() > 0 & e == null){
                    for (ParseObject object : objects) {
                        String userType = object.get("userType").toString();

                        if (userType.matches("Student")) {

                            Intent intent = new Intent(SignUpActivity.this, StudentDashboard.class);
                            startActivity(intent);

                        } else if (userType.matches("Chaplain")) {

                            Intent intent1 = new Intent(SignUpActivity.this, AdminDashboard.class);
                            startActivity(intent1);

                        }
                    }
                }
            }
        });*/

        if (radioButton.getText().toString().matches("Student")) {

            Intent intent = new Intent(SignUpActivity.this, StudentDashboard.class);
            startActivity(intent);

        } else if (radioButton.getText().toString().matches("Chaplain")) {

            Intent intent1 = new Intent(SignUpActivity.this, AdminDashboard.class);
            startActivity(intent1);

        }
    }

    public void signUpClicked(View view) {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);

        if (!validateUserType() | !validateEmail() | !validatePassword() | !validateMatric()) {
            return;
        }

        user.put("username", matricEditText.getText().toString());
        user.put("password", passwordEditText.getText().toString());
        user.put("email", emailEditText.getText().toString());
        user.put("userType", radioButton.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    if(radioButton.getText().toString().matches("Student")) {
                        Bitmap bitmap;
                        QRGEncoder qrgEncoder;
                        String inputValue= matricEditText.getText().toString().trim();

                        if(inputValue.length() > 0) {
                            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                            Display display = manager.getDefaultDisplay();
                            Point point = new Point();
                            display.getSize(point);
                            int width = point.x;
                            int height = point.y;
                            int smallerDimension = width < height ? width : height;
                            smallerDimension = smallerDimension * 3 / 4;
                            qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension);
                            try {
                                bitmap = qrgEncoder.encodeAsBitmap();
                                Log.i("Qr code", "Qr code generated");

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();

                                ParseFile file = new ParseFile("image.png", byteArray);

                                ParseObject object = new ParseObject("Image");

                                object.put("qrCode", file);
                                object.put("username", matricEditText.getText().toString());

                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i("Qr code", "Qr code generated successfully");
                                        } else {
                                            Log.i("Qr code failed", "Qr code generated failed");
                                        }
                                    }
                                });

                            } catch (Exception ex) {
                                Log.i("QR Code", ex.getMessage());
                            }
                        }
                    }

                    Bitmap profilePics = BitmapFactory.decodeResource(getResources(), R.drawable.profilepics);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    profilePics.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    ParseFile file = new ParseFile("image.png", byteArray);
                    user.put("displayImage", file);

                    user.put("department", " ");
                    user.put("name", " ");
                    user.put("chapel", " ");
                    user.put("phone", " ");
                    user.put("emails", emailEditText.getText().toString());
                    user.saveInBackground();
                    nextActivity();
                    MYToast.makeToast(SignUpActivity.this, "Sign up successfully",
                            MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();

                } else {
                    MYToast.makeToast(SignUpActivity.this, e.getMessage(),  MYToast.LENGTH_LONG,
                            MYToast.CUSTOM_TYPE_ERROR, MYToast.CUSTOM_GRAVITY_BOTTOM).show();
                }
            }
        });

    }

    public void loginClicked(View view) {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}