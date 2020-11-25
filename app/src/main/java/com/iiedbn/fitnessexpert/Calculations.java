package com.iiedbn.fitnessexpert;


import android.widget.Toast;

public class Calculations {

    public double getBreakfast() {
        return Breakfast;
    }
    public double getLunch() {
        return Lunch;
    }
    public double getDinner() {
        return Dinner;
    }
    public double getExercise() {
        return Exercise;
    }

    public Calculations(double breakfast, double lunch, double dinner, double exercise, double calorieAllowance) {
        Breakfast = breakfast;
        Lunch = lunch;
        Dinner = dinner;
        Exercise = exercise;
        CalorieAllowance = calorieAllowance;
    }

    public double Breakfast;
    public double Lunch;
    public double Dinner;
    public double Exercise;
    public double CalorieAllowance;

    public double getCalorieAllowance() {
        return CalorieAllowance;
    }
    public double TotalIntake()
    {
        double intake = getBreakfast() + getLunch() + getDinner();
        return intake;
    }

    public double CaloriesLeft()
    {
        double caloriesLeft = (getCalorieAllowance() - TotalIntake()) + getExercise();
        return caloriesLeft;
    }

}
