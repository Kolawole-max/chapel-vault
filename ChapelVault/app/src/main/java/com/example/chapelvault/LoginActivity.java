package com.example.chapelvault;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mustafayigit.mycustomtoast.MYToast;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private  String userType;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText matricEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        radioGroup = findViewById(R.id.radioGroup);
        matricEditText = findViewById(R.id.matricEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

    }

    public Boolean validateMatric() {
        if (matricEditText.getText().toString().trim().matches("")) {
            matricEditText.setError("Field can't be empty");
            return false;
        } else {
            matricEditText.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        if (passwordEditText.getText().toString().trim().matches("")) {
            passwordEditText.setError("Field can't be empty");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    public Boolean validateUserType() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            MYToast.makeToast(LoginActivity.this, "Select user type", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_WARNING, MYToast.CUSTOM_GRAVITY_CENTER).show();
            return false;
        } else {
            return true;
        }
    }

    //next activity
    public void nextActivity() {
        if (userType.matches("Student")) {
            Intent intent = new Intent(LoginActivity.this, StudentDashboard.class);
            startActivity(intent);
        } else if (userType.matches("Chaplain")) {
            Intent intent1 = new Intent(LoginActivity.this, AdminDashboard.class);
            startActivity(intent1);
        }
        finish();
    }

    //Login button
    public void loginClicked(View view) {
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioID);

        //if (radioButton.getText().toString()

        if (!validateMatric() | !validatePassword() | !validateUserType()) {
            return;
        }
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", matricEditText.getText().toString().trim());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        userType = object.getString("userType");
                    }
                }
            }
        });
        ParseUser.logInInBackground(matricEditText.getText().toString().trim(), passwordEditText.getText().toString().trim(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    MYToast.makeToast(LoginActivity.this, "Login successful", MYToast.LENGTH_LONG, MYToast.CUSTOM_TYPE_SUCCESS, MYToast.CUSTOM_GRAVITY_TOP).show();
                    nextActivity();
                }
            }
        });
    }

    public void signUpTextClicked(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}