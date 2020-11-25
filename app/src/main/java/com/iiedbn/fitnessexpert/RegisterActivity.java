package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    TextView Login;
    EditText mRegisterEmail, mRegisterPassword;
    Button mRegisterButton;
    TextView mSignIn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Login = findViewById(R.id.TVSignIN);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        mRegisterEmail = findViewById(R.id.TxbRegisterEmail);
        mRegisterPassword = findViewById(R.id.TxbRegisterPassword);
        mRegisterButton = findViewById(R.id.BtnRegister);
        mSignIn = findViewById(R.id.TVSignIN);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.RegisterProgress);

        //Checking if there isn't already a user logged in
        if (fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        try {
            //Getting the users desired email and password, then checking them to see if they have entered something
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = mRegisterEmail.getText().toString().trim();
                    String password = mRegisterPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        mRegisterEmail.setError("Email is required");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        mRegisterPassword.setError("Password is required");
                        return;
                    }
                    if (password.length() < 6) {
                        mRegisterPassword.setError("Password must be more than 6 characters");
                        return;
                    }

                    //Making the progress bar visible
                    progressBar.setVisibility(View.VISIBLE);

                    //Creating a new user with a built in firebase authentication method
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Launching a data gathering activity so that the user has data
                                Toast.makeText(getApplicationContext(), "Welcome! User has been Created.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DataGathering.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void startLogin()
    {
        RegisterActivity.this.finish();
    }
}