package com.iiedbn.fitnessexpert;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private CardView BtnRecipeLoad;
    private CardView BtnAddItem;
    private CardView BtnWorkoutLoad;
    private CardView BtnProfileLoad;
    private TextView Heading;
    private String userID;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Heading = findViewById(R.id.TVHeading);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        try {

            //This checks if the day is new and then resets all the calorie intakes back to 0 so that old data does not show up in a new days graph
            DocumentReference documentReference = fStore.collection("Users").document(userID);
            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    //This checks if the day is new and then resets all the calorie intakes back to 0 so that old data does not show up in a new days graph
                    boolean newDay;
                    long signedin = fAuth.getCurrentUser().getMetadata().getLastSignInTimestamp();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd");
                    String date = sdf.format(signedin);
                    if (value.getString("Date").contains(date)) {
                        Toast.makeText(MainActivity.this, "Welcome Back " + value.getString("First Name") + "!", Toast.LENGTH_SHORT).show();
                        newDay = false;
                    } else {
                        //This checks if the day is new and then resets all the calorie intakes back to 0 so that old data does not show up in a new days graph
                        newDay = true;
                        if (newDay) {
                            long signedin2 = fAuth.getCurrentUser().getMetadata().getLastSignInTimestamp();
                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
                            DocumentReference documentReference2 = fStore.collection("Users").document(userID);
                            documentReference2.update("Breakfast", 0);
                            documentReference2.update("Lunch", 0);
                            documentReference2.update("Dinner", 0);
                            documentReference2.update("Exercise Calories", 0);
                            documentReference2.update("Date", sdf.format(signedin));
                            documentReference2.update("Total Intake", 0);
                            documentReference2.update("Calories Left", 0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Its a new day, your calories have been reset to zero ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }


        try {
            //LOADING THE MAIN RECIPE SCREEN WHEN USER CLICKS TO VIEW RECIPES BUTTON
            BtnRecipeLoad = findViewById(R.id.BtnCardRecipe);
            BtnRecipeLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivityRecipeMainMenu();
                }
            });

            //LOADING THE PARENT ADD ITEM FORM WHEN THE USER CLICKS ADD ITEMS BUTTON
            BtnAddItem = findViewById(R.id.BtnCardAddItem);
            BtnAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivityAddItem();
                }
            });

            //LOADING THE WORKOUT TAB WHEN THE USER CLICKS ON WORKOUT BUTTON
            BtnWorkoutLoad = findViewById(R.id.BtnCardWorkOut);
            BtnWorkoutLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivityWorkout();
                }
            });

            BtnProfileLoad = findViewById(R.id.BtnCardProfile);
            BtnProfileLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openActivityProfile();
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "ERROR: " +ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void openActivityRecipeMainMenu()
    {
        Intent intent = new Intent(this, RecipeActivity.class);
        startActivity(intent);
    }

    public void openActivityAddItem()
    {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }

    public void openActivityWorkout()
    {
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    public void openActivityProfile()
    {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void Logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        MainActivity.this.finish();
    }
}