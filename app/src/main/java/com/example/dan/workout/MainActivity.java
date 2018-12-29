package com.example.dan.workout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    DBHelper MyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String name = intent.getStringExtra("exerciseName");
        TextView exerciseName = (TextView) findViewById(R.id.exercise_name_text_view);
        exerciseName.setText(name);

        MyDB = new DBHelper(this);

        EditText setEdit = (EditText) findViewById(R.id.sets_edit_text);
        EditText setTime = (EditText) findViewById(R.id.time_edit_text);

        //get previous values of sets and timer
        ArrayList<String> temp = MyDB.getFieldData(name);

        // if number of sets field is empty, prompt user to enter value
        if (temp.get(0) == null) {
//            setEdit.requestFocus();
            Log.d("SET", "SET IS NULL");
        }
        else {
            setEdit.setText(temp.get(0));
            setSetEditText(setEdit);
        }

        setEdit.requestFocus();
        setEdit.clearFocus();

        // display the time value if not empty
        if (temp.get(2) !=  null) {
            setTime.setText(temp.get(2));
            setTimeLeft(Integer.parseInt(temp.get(2)));
        }
    }

    CountDownTimer countingDownTimer;
    long timeLeft = 0;
    int setNumberMax = 0;
    int setNumber = 1;
    long tempTime = 0;


    public void startTimer(View v) {
        final TextView currentSet = (TextView) findViewById(R.id.current_set_text_view);
        currentSet.setVisibility(View.VISIBLE);

        final Button startButton = (Button) findViewById(R.id.start_button);

        String buttonString = startButton.getText().toString();
        if (buttonString.equals("Pause")) {
            startButton.setText("Resume");
            pauseTime();
            return;
        }
        else {
            startButton.setText("Pause");
        }

        countingDownTimer = new CountDownTimer(timeLeft, 1) {
            TextView timerDisplay = (TextView) findViewById(R.id.timer_display_text_view);

            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                double twoDecimalPlace = millisUntilFinished / 1000.0;
                timerDisplay.setText(String.format("%.2f", twoDecimalPlace));
            }

            public void onFinish() {
                if(setNumber <= setNumberMax) {
                    timerDisplay.setText("Set " + setNumber + " done.");
                    setNumber = setNumber + 1;
                    currentSet.setText("Current Set: " + (setNumber - 1));
                }
                if (setNumber > setNumberMax) {
                    endGameVisible();
                    timerDisplay.setText("All sets done.");
                } else {
                    startButton.setText("Start set " + setNumber);
                    currentSet.setText("Current Set: " + setNumber);
                }
                //             Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //             v.vibrate(1000);
                timeLeft = tempTime;
            }
        }.start();
    }

    public void pauseTime() {
        if (countingDownTimer != null) {
            countingDownTimer.cancel();
        }

    }

    public void resetTime(View v) {
        if (countingDownTimer != null) {
            countingDownTimer.cancel();

            timeLeft = tempTime;

            TextView timerDisplay = (TextView) findViewById(R.id.timer_display_text_view);
            timerDisplay.setText("Press Start");

            Button startButton = (Button) findViewById(R.id.start_button);
            startButton.setText("Start Set " + setNumber);
        }
    }

    public void endGameVisible() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //user cannot close dialog with back button
        builder.setCancelable(false);

        builder.setTitle("Well done!");

        builder.setIcon(R.drawable.checkmark);

        builder.setPositiveButton("Home", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();

                dialog.dismiss();
            }
        });
        builder.show();
    }


    public void restartApp(View v) {
        finish();
    }

    //clicking 'Update Sets' button triggers this function to update the max number of sets
    public void setSetEditText(View v) {

        EditText setEditText = (EditText) findViewById(R.id.sets_edit_text);

        try {
            setNumberMax = Integer.parseInt(setEditText.getText().toString());
        } catch (NumberFormatException e) {
//            Log.d("not integer", "error");
            Toast toast = Toast.makeText(this, "Enter an integer value.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 300);
            toast.show();
            return;
        }

        //update DB with new sets
        TextView exerciseName = (TextView) findViewById(R.id.exercise_name_text_view);
        MyDB.updateSets(exerciseName.getText().toString(), setEditText.getText().toString());

    }

    //triggered by clicking on 'Update' button for the timer. Updates the rest time limit, and stored in 'timeLeft' variable
    public void updateTimeLeft(View v) {

        EditText timeEditText = (EditText) findViewById(R.id.time_edit_text);
        final InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 400);
        TextView message = toast.getView().findViewById(android.R.id.message);

        try {
            int inputTime = Integer.parseInt(timeEditText.getText().toString());
            setTimeLeft(inputTime);
//            Log.d("time: ", ""+timeLeft);
        }
        catch(NumberFormatException e){
//            Log.d("not integer","error");
            toast.setText("Enter an integer value.");
            message.setTextColor(Color.RED);
            toast.show();
            return;
        }

        TextView exerciseName = (TextView) findViewById(R.id.exercise_name_text_view);

        try {
            MyDB.updateTimer(exerciseName.getText().toString(), timeEditText.getText().toString());
        }
        catch (Exception e) {
            toast.setText("Failed to update");
            message.setTextColor(Color.RED);
            toast.show();
            return;
        }

        toast.setText("Updated rest time");
        message.setTextColor(Color.GREEN);
        toast.show();

        timeEditText.clearFocus();
        imm.hideSoftInputFromWindow(timeEditText.getWindowToken(), 0);
    }


    public void setTimeLeft(int time) {
        timeLeft = time * 1000;
        tempTime = timeLeft;
    }
}