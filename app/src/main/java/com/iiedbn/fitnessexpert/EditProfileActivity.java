package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText FirstName;
    private EditText Surname;
    private EditText CalorieGoal;
    private EditText CurrentWeight;
    private EditText GoalWeight;
    private EditText Height;
    private EditText GoalHeight;
    private Spinner Sex;
    private Spinner Measurement;
    private Button SaveData;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        try {
            Sex = findViewById(R.id.SpinEditSex);
            List<String> gender = new ArrayList<>();
            String male = "Male";
            String female = "Female";
            gender.add(male);
            gender.add(female);

            //Setting an adapter so that the spinner displays dropdown items in the array
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, gender);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Sex.setAdapter(adapter);
            Sex.setSelection(0);


            Measurement = findViewById(R.id.SpinEditMeasurement);
            List<String> measurement = new ArrayList<>();
            String metric = "Metric";
            String imperial = "Imperial";
            measurement.add(metric);
            measurement.add(imperial);

            //Setting an adapter so that the spinner displays dropdown items in the array
            ArrayAdapter<String> adapterMeasure = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, measurement);
            adapterMeasure.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Measurement.setAdapter(adapterMeasure);
            Measurement.setSelection(0);

            String measure = Measurement.getSelectedItem().toString();

            if (measure == "Metric") {
                Toast.makeText(this, "Enter values in kilograms and centimeters, or choose Imperial to measure in pounds and inches", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        FirstName = findViewById(R.id.ETEditFirstName);
        Surname = findViewById(R.id.ETEditSurname);
        CalorieGoal = findViewById(R.id.ETEditCalorieGoal);
        CurrentWeight = findViewById(R.id.ETEditCurrentWeight);
        GoalWeight = findViewById(R.id.ETEditGoalWeight);
        Height = findViewById(R.id.ETEditHeight);
        GoalHeight = findViewById(R.id.ETEditGoalHeight);
        SaveData = findViewById(R.id.BtnEditProfile);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        SaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEdit();
            }
        });
    }

    public void startEdit() {
        String first = FirstName.getText().toString();
        String surname = Surname.getText().toString();
        double calorieGoal = Integer.parseInt(CalorieGoal.getText().toString());
        double currentWeight = Integer.parseInt(CurrentWeight.getText().toString());
        double goalWeight = Integer.parseInt(GoalWeight.getText().toString());
        double height = Integer.parseInt(Height.getText().toString());
        double goalHeight = Integer.parseInt(GoalHeight.getText().toString());
        String sex = Sex.getSelectedItem().toString();
        String measurementData = Measurement.getSelectedItem().toString();

        try
        {

            DocumentReference documentReference = fStore.collection("Users").document(userID);

            //Makes sure the user has entered values
            if (TextUtils.isEmpty(first) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(CalorieGoal.getText()) || TextUtils.isEmpty(CurrentWeight.getText()) || TextUtils.isEmpty(GoalWeight.getText()) || TextUtils.isEmpty(sex) || TextUtils.isEmpty(Height.getText()) || CalorieGoal.getText().toString() == "" || first == "" || surname == "" || GoalWeight.getText().toString() == "0" || CurrentWeight.getText().toString() == "0" || sex == "" || GoalHeight.getText().toString() == "0") {
                Toast.makeText(this, "Values need to be entered for all the fields above to continue!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                //Makes different conversions depending on what the user has chosen
                if (measurementData == "Metric") {
                    double currentWeightMetric = Math.round(currentWeight);
                    double currentWeightImperial = Math.round(currentWeight * 2.2);

                    double goalWeightMetric = Math.round(goalWeight);
                    double goalWeightImperial = Math.round(goalWeight * 2.2);

                    double currentHeightMetric = Math.round(height);
                    double currentHeightImperial = Math.round((height * 3.28) / 100);

                    double goalHeightMetric = Math.round(goalHeight);
                    double goalHeightImperial = Math.round((goalHeight * 3.28) / 100);

                    documentReference.update("Measurement System", measurementData);

                    //updating the document depending on the fields
                    documentReference.update("Current Weight Metric", currentWeightMetric);
                    documentReference.update("Goal Weight Metric", goalWeightMetric);
                    documentReference.update("Height Metric", currentHeightMetric);
                    documentReference.update("Goal Height Metric", goalHeightMetric);

                    //updating the document depending on the fields
                    documentReference.update("Current Weight Imperial", currentWeightImperial);
                    documentReference.update("Goal Weight Imperial", goalWeightImperial);
                    documentReference.update("Height Imperial", currentHeightImperial);
                    documentReference.update("Goal Height Imperial", goalHeightImperial).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Edit is Complete", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                    ;
                    //Makes different conversions depending on what the user has chosen
                } else if (measurementData == "Imperial") {
                    double currentWeightMetric = Math.round(currentWeight / 2.2);
                    double currentWeightImperial = Math.round(currentWeight);

                    double goalWeightMetric = Math.round(goalWeight / 2.2);
                    double goalWeightImperial = Math.round(goalWeight);

                    double currentHeightMetric = Math.round((height / 3.28) * 100);
                    double currentHeightImperial = Math.round((height));

                    double goalHeightMetric = Math.round((goalHeight / 3.28) * 100);
                    double goalHeightImperial = Math.round((goalHeight));

                    documentReference.update("Measurement System", measurementData);

                    //updating the document depending on the fields
                    documentReference.update("Current Weight Metric", currentWeightMetric);
                    documentReference.update("Goal Weight Metric", goalWeightMetric);
                    documentReference.update("Height Metric", currentHeightMetric);
                    documentReference.update("Goal Height Metric", goalHeightMetric);

                    //updating the document depending on the fields
                    documentReference.update("Current Weight Imperial", currentWeightImperial);
                    documentReference.update("Goal Weight Imperial", goalWeightImperial);
                    documentReference.update("Height Imperial", currentHeightImperial);
                    documentReference.update("Goal Height Imperial", goalHeightImperial).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Edit is Complete", Toast.LENGTH_LONG).show();

                        }
                    });
                }
                documentReference.update("Calorie Allowance", calorieGoal);
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Getting the current date and converting it to a format
        long millis = System.currentTimeMillis();
        Date signedIn = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (measurementData == "Metric") {
            String addWeightMetric = "" + sdf.format(signedIn) + " was " + Math.round(currentWeight);
            String addWeightImperial = "" + sdf.format(signedIn) + " was " + Math.round(currentWeight * 2.2);

            //updating the arrays that contain the weight logs
            DocumentReference documentReference = fStore.collection("Weight Logs").document(userID);
            documentReference.update("MetricLog", FieldValue.arrayUnion(addWeightMetric)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "A new weight log has been added!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            //updating the arrays that contain the weight logs
            DocumentReference documentReference2 = fStore.collection("Weight Logs").document(userID);
            documentReference2.update("ImperialLog", FieldValue.arrayUnion(addWeightImperial)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
        else if (measurementData == "Imperial")
        {
            String addWeightMetric = "" + sdf.format(signedIn) + " was " + Math.round(currentWeight / 2.2);
            String addWeightImperial = "" + sdf.format(signedIn) + " was " + Math.round(currentWeight);

            //updating the arrays that contain the weight logs
            DocumentReference documentReference = fStore.collection("Weight Logs").document(userID);
            documentReference.update("MetricLog", FieldValue.arrayUnion(addWeightMetric)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "A new weight log has been added!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
            //updating the arrays that contain the weight logs
            DocumentReference documentReference2 = fStore.collection("Weight Logs").document(userID);
            documentReference2.update("ImperialLog", FieldValue.arrayUnion(addWeightImperial)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
        }
    }
}