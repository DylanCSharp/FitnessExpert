package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
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

public class LoginActivity extends AppCompatActivity {

    private TextView Register;
    private EditText Email, Password;
    private Button BtnLogin;
    private ProgressBar ProgressBarLogin;

    //Getting fire authentication
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        BtnLogin = findViewById(R.id.BtnLogin);
        Email = findViewById(R.id.TxbEmail);
        Password = findViewById(R.id.TxbPassword);
        ProgressBarLogin = findViewById(R.id.LoginProgress);
        Register = findViewById(R.id.TVRegister);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoggedIn();
            }
        });
    }

    public void startRegister()
    {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    public void LoggedIn()
    {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        try {
            //Checking to see if the user has entered and email address
            if (TextUtils.isEmpty(email) || email == "") {
                Email.setError("Email is required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Password.setError("Password is required");
                return;
            }

            ProgressBarLogin.setVisibility(View.VISIBLE);

            //Signing the user in with a built in firebase auth method
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        ProgressBarLogin.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}