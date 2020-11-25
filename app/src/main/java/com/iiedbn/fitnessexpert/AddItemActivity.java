package com.iiedbn.fitnessexpert;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class AddItemActivity extends AppCompatActivity {

    private EditText Breakfast;
    private EditText Lunch;
    private EditText Dinner;
    private EditText Exercise;
    private ImageView BtnAdd;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;

    private double CalorieAllowance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //getting the instances from firestore so that we can use them
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //instantiating the variables
        Breakfast = findViewById(R.id.ETAddBreakfast);
        Lunch = findViewById(R.id.ETAddLunch);
        Dinner = findViewById(R.id.ETAddDinner);
        Exercise = findViewById(R.id.ETAddExercise);
        BtnAdd = findViewById(R.id.IconAddAll);

        try {
            //Getting the calorie allowance so that we can use it to make calculations
            DocumentReference documentReference = fStore.collection("Users").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    CalorieAllowance = value.getDouble("Calorie Allowance");
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void addingItem(View view) {

        try {
            //Getting when last they signed in and adding it to the document, so that we are able to reset the days
            long signedin2 = fAuth.getCurrentUser().getMetadata().getLastSignInTimestamp();
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd");

            DocumentReference documentReference = fStore.collection("Users").document(userID);

            //updating the fields in the document within firestore
            documentReference.update("Breakfast", Integer.parseInt(Breakfast.getText().toString()));
            documentReference.update("Lunch", Integer.parseInt(Lunch.getText().toString()));
            documentReference.update("Dinner", Integer.parseInt(Dinner.getText().toString()));
            documentReference.update("Exercise Calories", Integer.parseInt(Exercise.getText().toString()));
            documentReference.update("Date", sdf2.format(signedin2));

            double breakfast = Double.parseDouble(Breakfast.getText().toString());
            double lunch = Double.parseDouble(Lunch.getText().toString());
            double dinner = Double.parseDouble(Dinner.getText().toString());
            double exercise = Double.parseDouble(Exercise.getText().toString());

            //sending values to my calculations class which calculates calorie intake and calories left for the day
            Calculations calculations = new Calculations(breakfast, lunch, dinner, exercise, CalorieAllowance);

            documentReference.update("Total Intake", calculations.TotalIntake());
            documentReference.update("Calories Left", calculations.CaloriesLeft()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddItemActivity.this, "Add Complete, you have " + calculations.CaloriesLeft() + " left for the day", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


