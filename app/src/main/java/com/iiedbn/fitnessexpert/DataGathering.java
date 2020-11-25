package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGathering extends AppCompatActivity {

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
        setContentView(R.layout.activity_data_gathering);

        try {
            //Setting the array of sex's
            Sex = findViewById(R.id.SpinSex);
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


            //Setting the array of measurements
            Measurement = findViewById(R.id.SpinMeasurement);
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

            //Warns the user that metric is selected and they should enter materic values
            if (measure == "Metric") {
                Toast.makeText(this, "Enter values in kilograms and centimeters, or choose Imperial to measure in pounds and inches", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        FirstName = findViewById(R.id.ETDataFirstName);
        Surname = findViewById(R.id.ETDataSurname);
        CalorieGoal = findViewById(R.id.ETDataCalorieGoal);
        CurrentWeight = findViewById(R.id.ETDataCurrentWeight);
        GoalWeight = findViewById(R.id.ETDataGoalWeight);
        Height = findViewById(R.id.ETDataHeight);
        GoalHeight = findViewById(R.id.ETDataGoalHeight);
        SaveData = findViewById(R.id.BtnGatherData);

        //Getting the firestore instances
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        SaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUserData();
            }
        });
    }

    public void createUserData() {

        //Creating variables
        String first = FirstName.getText().toString();
        String surname = Surname.getText().toString();
        double calorieGoal = Integer.parseInt(CalorieGoal.getText().toString());
        double currentWeight = Integer.parseInt(CurrentWeight.getText().toString());
        double goalWeight = Integer.parseInt(GoalWeight.getText().toString());
        double height = Integer.parseInt(Height.getText().toString());
        double goalHeight = Integer.parseInt(GoalHeight.getText().toString());
        String sex = Sex.getSelectedItem().toString();
        String measurementData = Measurement.getSelectedItem().toString();

        try {
            //Making sure the user has entered values for all the fields
            if (TextUtils.isEmpty(first) || TextUtils.isEmpty(surname) || TextUtils.isEmpty(CalorieGoal.getText()) || TextUtils.isEmpty(CurrentWeight.getText()) || TextUtils.isEmpty(GoalWeight.getText()) || TextUtils.isEmpty(sex) || TextUtils.isEmpty(Height.getText()) || CalorieGoal.getText().toString() == "" || first == "" || surname == "" || GoalWeight.getText().toString() == "" || CurrentWeight.getText().toString() == "" || sex == "" || GoalHeight.getText().toString() == "") {
                Toast.makeText(this, "Values need to be entered for all the fields above to continue!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                DocumentReference documentReference = fStore.collection("Users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("Breakfast", 0);
                user.put("Calorie Allowance", calorieGoal);

                //If the user chooses metric, the right conversions are made for metric and imperial values
                //Both imperial and metric values for the users data will be stored so these calculations convert according to what measurement system the user has chosen
                if (measurementData == "Metric") {
                    double currentWeightMetric = Math.round(currentWeight);
                    double currentWeightImperial = Math.round(currentWeight * 2.2);

                    double goalWeightMetric = Math.round(goalWeight);
                    double goalWeightImperial = Math.round(goalWeight * 2.2);

                    double currentHeightMetric = Math.round(height);
                    double currentHeightImperial = Math.round((height * 3.28) / 100);

                    double goalHeightMetric = Math.round(goalHeight);
                    double goalHeightImperial = Math.round((goalHeight * 3.28) / 100);

                    //Adding the values to the map which will then be added to the document
                    user.put("Current Weight Metric", currentWeightMetric);
                    user.put("Goal Weight Metric", goalWeightMetric);
                    user.put("Height Metric", currentHeightMetric);
                    user.put("Goal Height Metric", goalHeightMetric);

                    //Adding the values to the map which will then be added to the document
                    user.put("Current Weight Imperial", currentWeightImperial);
                    user.put("Goal Weight Imperial", goalWeightImperial);
                    user.put("Height Imperial", currentHeightImperial);
                    user.put("Goal Height Imperial", goalHeightImperial);

                    //If the user chooses metric, the right conversions are made for metric and imperial values
                    //Both imperial and metric values for the users data will be stored so these calculations convert according to what measurement system the user has chosen
                } else if (measurementData == "Imperial") {
                    double currentWeightMetric = Math.round(currentWeight / 2.2);
                    double currentWeightImperial = Math.round(currentWeight);

                    double goalWeightMetric = Math.round(goalWeight / 2.2);
                    double goalWeightImperial = Math.round(goalWeight);

                    double currentHeightMetric = Math.round((height / 3.28) * 100);
                    double currentHeightImperial = Math.round((height));

                    double goalHeightMetric = Math.round((goalHeight / 3.28) * 100);
                    double goalHeightImperial = Math.round((goalHeight));

                    //Adding the values to the map which will then be added to the document
                    user.put("Current Weight Metric", currentWeightMetric);
                    user.put("Goal Weight Metric", goalWeightMetric);
                    user.put("Height Metric", currentHeightMetric);
                    user.put("Goal Height Metric", goalHeightMetric);

                    //Adding the values to the map which will then be added to the document
                    user.put("Current Weight Imperial", currentWeightImperial);
                    user.put("Goal Weight Imperial", goalWeightImperial);
                    user.put("Height Imperial", currentHeightImperial);
                    user.put("Goal Height Imperial", goalHeightImperial);
                }

                //Adding the values to the map which will then be added to the document
                //Also getting the current time and inserting it into the document
                long signedin2 = fAuth.getCurrentUser().getMetadata().getLastSignInTimestamp();
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
                user.put("Date", sdf2.format(signedin2));
                user.put("Dinner", 0);
                user.put("Exercise Calories", 0);
                user.put("First Name", first);
                user.put("Lunch", 0);
                user.put("Measurement System", measurementData);
                user.put("Sex", sex);
                user.put("Surname", surname);
                user.put("Total Intake", 0);
                user.put("Calories Left", calorieGoal);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DataGathering.this, "Data has been gathered and stored!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DataGathering.this, "Date could not be gathered. Something went wrong...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                });
            }
        } catch (Exception ex) {
            Toast.makeText(this, "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //This next bit of code also adds values to the weight logs section so that the user can also view their weight logs
        long millis = System.currentTimeMillis();
        Date signedIn = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        DocumentReference documentReference = fStore.collection("Weight Logs").document(userID);
        Map<String, Object> weightlogs = new HashMap<>();

        //Makes different conversions depending on what the user has chosen
        if (measurementData == "Metric"){

            double currentWeightMetric = Math.round(currentWeight);
            double currentWeightImperial = Math.round(currentWeight * 2.2);

            ArrayList metricLogs = new ArrayList();
            metricLogs.add(sdf.format(signedIn) +" was "  + Math.round(currentWeightMetric));

            ArrayList imperialLogs = new ArrayList();
            imperialLogs.add(sdf.format(signedIn) +" was "  + Math.round(currentWeightImperial));

            weightlogs.put("ImperialLog",imperialLogs);
            weightlogs.put("MetricLog", metricLogs);

            documentReference.set(weightlogs).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DataGathering.this, "First weight log has been captured!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //Makes different conversions depending on what the user has chosen
        else if (measurementData == "Imperial")
        {
            double currentWeightImperial = Math.round(currentWeight);
            double currentWeightMetric = Math.round(currentWeight / 2.2);

            ArrayList imperialLogs = new ArrayList();
            imperialLogs.add(sdf.format(signedIn) +" was "  + Math.round(currentWeightImperial));

            ArrayList metricLogs = new ArrayList();
            metricLogs.add(sdf.format(signedIn) +" was "  + Math.round(currentWeightMetric));

            weightlogs.put("ImperialLog",imperialLogs);
            weightlogs.put("MetricLog", metricLogs);

            documentReference.set(weightlogs).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(DataGathering.this, "First weight log has been captured!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}