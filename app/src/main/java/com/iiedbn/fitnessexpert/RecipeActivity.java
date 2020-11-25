package com.iiedbn.fitnessexpert;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class RecipeActivity extends AppCompatActivity {

    private FirebaseFirestore fStore;
    private TextView Breakfast;
    private TextView Lunch;
    private TextView Dinner;
    private ProgressBar BreakfastProgress, LunchProgress, DinnerProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //Instantiating the variables
        Breakfast = findViewById(R.id.TVBreakfast);
        Lunch = findViewById(R.id.TVLunch);
        Dinner = findViewById(R.id.TVDinner);
        BreakfastProgress = findViewById(R.id.BreakfastProgress);
        LunchProgress = findViewById(R.id.LunchProgress);
        DinnerProgress = findViewById(R.id.DinnerProgress);

        fStore = FirebaseFirestore.getInstance();

        try {
            //Getting the document reference for recipes and then setting text views to the fields
            //DISPLAYING ALL THE RECIPES
            fStore.collection("Recipe").document("FitnessExpert").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        BreakfastProgress.setVisibility(View.INVISIBLE);
                        LunchProgress.setVisibility(View.INVISIBLE);
                        DinnerProgress.setVisibility(View.INVISIBLE);
                        Breakfast.setText(task.getResult().getString("Breakfast"));
                        Lunch.setText(task.getResult().getString("Lunch"));
                        Dinner.setText(task.getResult().getString("Dinner"));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Breakfast.setText("Breakfast Data could not be retrieved");
                    Lunch.setText("Lunch Data could not be retrieved");
                    Dinner.setText("Dinner Data could not be retrieved");
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}