package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    String userID;
    private TextView CurrentHeight;
    private TextView GoalHeight;
    private TextView CurrentWeight;
    private TextView GoalWeight;
    private TextView CalorieAllowance;
    private TextView CaloriesLeft;
    private TextView MeasurementSystem;
    private LinearLayout LinearLay;

    private Button BtnLaunchEdit;
    private ImageButton BtnLaunchUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        LinearLay = findViewById(R.id.LinearLay);


        try {

            DocumentReference documentReference = fStore.collection("Users").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                    CurrentWeight = findViewById(R.id.TVProfileCurrentWeight);
                    GoalWeight = findViewById(R.id.TVProfileGoalWeight);

                    CurrentHeight = findViewById(R.id.TVProfileCurrentHeight);
                    GoalHeight = findViewById(R.id.TVProfileGoalHeight);

                    CalorieAllowance = findViewById(R.id.TVProfileCalorieAllowance);
                    CaloriesLeft = findViewById(R.id.TVProfileCaloriesLeft);
                    MeasurementSystem = findViewById(R.id.TVProfileMeasure);
                    BtnLaunchEdit = findViewById(R.id.BtnLaunchEditProfile);

                    try {
                        String system = value.getString("Measurement System");

                        if (system.contains("M")) {
                            CurrentHeight.setText("Current Height:  " + value.getLong("Height Metric").toString() + " cm's");
                            GoalHeight.setText("Goal Height:  " + value.getLong("Goal Height Metric").toString() + " cm's");

                            CurrentWeight.setText("Current Weight:  " + value.getLong("Current Weight Metric").toString() + " KG's");
                            GoalWeight.setText("Goal Weight:  " + value.getLong("Goal Weight Metric").toString() + " KG's");

                            CalorieAllowance.setText("Calorie Allowance:  " + value.getLong("Calorie Allowance").toString());
                            CaloriesLeft.setText(value.getLong("Calories Left").toString() + " calories left for the day");
                            MeasurementSystem.setText("Measurement System:  " + value.getString("Measurement System"));

                        } else if (system.contains("I")) {
                            CurrentHeight.setText("Current Height:  " + value.getLong("Height Imperial").toString() + " Feet");
                            GoalHeight.setText("Goal Height:  " + value.getLong("Goal Height Imperial").toString() + " Feet");

                            CurrentWeight.setText("Current Weight:  " + value.getLong("Current Weight Imperial").toString() + " Pound's");
                            GoalWeight.setText("Goal Weight:  " + value.getLong("Goal Weight Imperial").toString() + " Pound's");

                            CalorieAllowance.setText("Calorie Allowance:  " + value.getLong("Calorie Allowance").toString());
                            CaloriesLeft.setText(value.getLong("Calories Left").toString() + " calories left for the day");
                            MeasurementSystem.setText("Measurement System: " + value.getString("Measurement System"));
                        }

                        BarChart barChart = findViewById(R.id.BarGraphWeight);

                        ArrayList<BarEntry> values = new ArrayList<>();
                        values.add(new BarEntry(1, value.getLong("Current Weight Metric")));
                        values.add(new BarEntry(2, value.getLong("Goal Weight Metric")));
                        values.add(new BarEntry(3, value.getLong("Current Weight Imperial")));
                        values.add(new BarEntry(4, value.getLong("Goal Weight Imperial")));

                        BarDataSet barDataSet = new BarDataSet(values, "");
                        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSet.setValueTextColor(Color.BLACK);
                        barDataSet.setValueTextSize(12f);

                        BarData barData = new BarData(barDataSet);

                        barChart.setFitBars(true);
                        barChart.setData(barData);
                        barChart.getDescription().setText("");
                        barChart.animateY(1000);

                        BarChart barChartHeight = findViewById(R.id.BarGraphHeight);

                        ArrayList<BarEntry> valuesHeight = new ArrayList<>();
                        valuesHeight.add(new BarEntry(1, value.getLong("Height Metric")));
                        valuesHeight.add(new BarEntry(2, value.getLong("Goal Height Metric")));
                        valuesHeight.add(new BarEntry(3, value.getLong("Height Imperial")));
                        valuesHeight.add(new BarEntry(4, value.getLong("Goal Height Imperial")));

                        BarDataSet barDataSetHeight = new BarDataSet(valuesHeight, "");
                        barDataSetHeight.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSetHeight.setValueTextColor(Color.BLACK);
                        barDataSetHeight.setValueTextSize(12f);

                        BarData barDataHeight = new BarData(barDataSetHeight);

                        barChartHeight.setFitBars(true);
                        barChartHeight.setData(barDataHeight);
                        barChartHeight.getDescription().setText("");
                        barChartHeight.animateY(1000);

                        BarChart barChartCalorie = findViewById(R.id.BarGraphCalorie);

                        ArrayList<BarEntry> valuesCalories = new ArrayList<>();
                        valuesCalories.add(new BarEntry(1, value.getLong("Breakfast")));
                        valuesCalories.add(new BarEntry(2, value.getLong("Lunch")));
                        valuesCalories.add(new BarEntry(3, value.getLong("Dinner")));
                        valuesCalories.add(new BarEntry(4, value.getLong("Exercise Calories")));
                        valuesCalories.add(new BarEntry(5, value.getLong("Total Intake")));
                        valuesCalories.add(new BarEntry(6, value.getLong("Calorie Allowance")));
                        valuesCalories.add(new BarEntry(7, value.getLong("Calories Left")));

                        BarDataSet barDataSetCalorie = new BarDataSet(valuesCalories, "");
                        barDataSetCalorie.setColors(ColorTemplate.MATERIAL_COLORS);
                        barDataSetCalorie.setValueTextColor(Color.BLACK);
                        barDataSetCalorie.setValueTextSize(12f);

                        BarData barDataCalorie = new BarData(barDataSetCalorie);

                        barChartCalorie.setFitBars(true);
                        barChartCalorie.setData(barDataCalorie);
                        barChartCalorie.getDescription().setText("");
                        barChartCalorie.animateY(1000);
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    BtnLaunchEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editProfile();
                        }
                    });

                    BtnLaunchUpload = findViewById(R.id.BtnLaunchUpload);
                    BtnLaunchUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchUpload();
                        }
                    });
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try
        {
            //CYCLING THROUGH THE ARRAYS WITHIN FIRESTORE AND DISPLAYING THEM IN TEXT VIEWS
            DocumentReference documentReference = fStore.collection("Users").document(userID);
            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.getString("Measurement System").contains("M"))
                    {
                        fStore.collection("Weight Logs").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    ArrayList listDate = (ArrayList) documentSnapshot.get("MetricLog");

                                    //CYCLING THROUGH THE ARRAYS WITHIN FIRESTORE AND DISPLAYING THEM IN TEXT VIEWS
                                    for (int i = 0; i < listDate.size(); i++) {
                                        TextView value = new TextView(getApplicationContext());
                                        value.setText("Weight on " + listDate.get(i));
                                        value.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        value.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                                        value.setGravity(Gravity.CENTER);
                                        LinearLay.addView(value);
                                    }
                                }
                            }
                        });
                    }
                    //CYCLING THROUGH THE ARRAYS WITHIN FIRESTORE AND DISPLAYING THEM IN TEXT VIEWS
                    else {
                        fStore.collection("Weight Logs").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();

                                    ArrayList listDate = (ArrayList) documentSnapshot.get("ImperialLog");

                                    //CYCLING THROUGH THE ARRAYS WITHIN FIRESTORE AND DISPLAYING THEM IN TEXT VIEWS
                                    for (int i = 0; i < listDate.size(); i++) {
                                        TextView value = new TextView(getApplicationContext());
                                        value.setText("Weight on " + listDate.get(i));
                                        value.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                        value.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                                        value.setGravity(Gravity.CENTER);
                                        LinearLay.addView(value);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(this, "" +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //STARTING NEW INTENTS
    public void editProfile()
    {
        startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
    }

    public void launchUpload(){
        startActivity(new Intent(getApplicationContext(), UploadImage.class));
    }

}