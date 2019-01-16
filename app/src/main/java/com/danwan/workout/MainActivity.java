package com.danwan.workout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.danwan.workout.dialog.SettingsDialog;
import com.danwan.workout.helper.DBHelper;
import com.danwan.workout.model.Settings;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    long timeLeft = 0;
    int setNumberMax = 0;
    int setNumber = 1;
    long tempTime = 0;
    CountDownTimer countingDownTimer;
    DBHelper MyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra("exerciseName");
        TextView exerciseName = findViewById(R.id.exercise_name_text_view);
        exerciseName.setText(name);

        MyDB = new DBHelper(this);

        EditText setEdit = findViewById(R.id.sets_edit_text);
        EditText setTime = findViewById(R.id.time_edit_text);

        //get previous values of sets and timer
        ArrayList<String> temp = MyDB.getFieldData(name);

        // if number of sets field is empty, prompt user to enter value
        if (temp.get(0) == null) {
            Log.d("SET", "SET IS NULL");
        }
        else {
            setEdit.setText(temp.get(0));
            setSetMax(Integer.parseInt(temp.get(0)));
        }

        setEdit.requestFocus();
        setEdit.clearFocus();

        // display the time value if not empty
        if (temp.get(2) !=  null) {
            setTime.setText(temp.get(2));
            setTimeLeft(Integer.parseInt(temp.get(2)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            SettingsDialog settingsDialog = new SettingsDialog(this);
            settingsDialog.showSettingsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTimer(View v) {
        final TextView currentSet = findViewById(R.id.current_set_text_view);
        currentSet.setVisibility(View.VISIBLE);

        final Button startButton = findViewById(R.id.start_button);

        String buttonString = startButton.getText().toString();
        if (buttonString.equals("Pause")) {
            startButton.setText(R.string.resume);
            pauseTime();
            return;
        }
        else {
            startButton.setText(R.string.pause);
        }

        countingDownTimer = new CountDownTimer(timeLeft, 1) {
            TextView timerDisplay = (TextView) findViewById(R.id.timer_display_text_view);

            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                double twoDecimalPlace = millisUntilFinished / 1000.0;
                timerDisplay.setText(String.format("%.2f", twoDecimalPlace));
            }

            public void onFinish() {
                finishSet(timerDisplay, currentSet, startButton);
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

        // if timer is running, cancel it
        if (countingDownTimer != null) {
            countingDownTimer.cancel();
            timeLeft = tempTime;
        }

        TextView currentSet = findViewById(R.id.current_set_text_view);
        TextView timerDisplay = findViewById(R.id.timer_display_text_view);
        Button startButton = findViewById(R.id.start_button);

        finishSet(timerDisplay, currentSet, startButton);
    }

    public void finishSet(TextView timerDisplay, TextView currentSet, Button startButton) {

        if (setNumber <= setNumberMax) {
            timerDisplay.setText("Set " + setNumber + " done.");
            setNumber = setNumber + 1;
            currentSet.setText("Current Set: " + (setNumber - 1));
        }
        if (setNumber > setNumberMax) {
            endGameVisible();
            timerDisplay.setText("All sets done.");
        } else {
            startButton.setText("Rest");
            currentSet.setText("Current Set: " + setNumber);
        }

        // get the settings
        Settings settings = MyDB.getSettings();

        // trigger vibrate and/or audio if set in settings
        if (settings.isVibrate()) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(1000);
        }
        if (settings.getVolume() > 0) {
            MediaPlayer mp = MediaPlayer.create(MainActivity.this, R.raw.ready_sound);
            float volume = (float) (settings.getVolume() / 100.0);
            mp.setVolume(volume, volume);
            mp.start();
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
    public void updateSetEditText(View v) {
        EditText editText = findViewById(R.id.sets_edit_text);
        updateSetsOrTime(editText, "sets");
    }

    //triggered by clicking on 'Update' button for the timer. Updates the rest time limit, and stored in 'timeLeft' variable
    public void updateTimeLeft(View v) {
        EditText editText = findViewById(R.id.time_edit_text);
        updateSetsOrTime(editText, "time");
    }

    public void updateSetsOrTime(EditText editText, String field) {

        if (!field.equals("time") || !field.equals("sets")) {
            Log.d("updateSetsOrTime", "field is not 'sets' or 'time'");
        }

        Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 400);
        TextView message = toast.getView().findViewById(android.R.id.message);

        try {
            int value = Integer.parseInt(editText.getText().toString());
            if (field.equals("time")) {
                setTimeLeft(value);
            }
            else {
                setSetMax(value);
            }
        }
        catch(NumberFormatException e){
            toast.setText("Enter an integer value.");
            message.setTextColor(Color.RED);
            toast.show();
            return;
        }

        TextView exerciseName = findViewById(R.id.exercise_name_text_view);

        try {
            if (field.equals("time")) {
                MyDB.updateTimer(exerciseName.getText().toString(), editText.getText().toString());
            }
            else {
                MyDB.updateSets(exerciseName.getText().toString(), editText.getText().toString());
            }
        }
        catch (Exception e) {
            toast.setText("Failed to update");
            message.setTextColor(Color.RED);
            toast.show();
            return;
        }

        if (field.equals("time")) {
            toast.setText("Updated rest time");
        }
        else {
            toast.setText("Updated sets");
        }
        message.setTextColor(Color.GREEN);
        toast.show();
        editText.clearFocus();
        hideKeyboard(editText);
    }


    public void setTimeLeft(int time) {
        timeLeft = time * 1000;
        tempTime = timeLeft;
    }

    public void setSetMax(int num) {
        setNumberMax = num;
    }

    public void hideKeyboard(View v) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}