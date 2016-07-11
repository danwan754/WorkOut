package com.example.dan.workout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

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

        TextView setEdit = (TextView) findViewById(R.id.sets_edit_text);
        TextView setTime = (TextView) findViewById(R.id.time_edit_text);

        //get previous values of sets and timer
        ArrayList<String> temp = MyDB.getFieldData(name);

        setEdit.setText(temp.get(0));
        setSetEditText(setEdit);

        setTime.setText(temp.get(2));
        setTimeLeft(setTime);

    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    CountDownTimer countingDownTimer;
    long timeLeft = 0;
    int setNumberMax = 0;
    int setNumber = 1;
    long tempTime = 0;


    public void startTimer(View v) {
        final TextView currentSet = (TextView) findViewById(R.id.currentPushUp_set_text_view);
        currentSet.setVisibility(View.VISIBLE);

        final Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setVisibility(View.INVISIBLE);

        final Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setVisibility(View.VISIBLE);

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
                    startButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.INVISIBLE);
                    currentSet.setText("Current Set: " + setNumber);
                }
                //             Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //             v.vibrate(1000);
                timeLeft = tempTime;
            }
        }.start();
    }

    public void pauseTime(View v) {
        if (countingDownTimer != null) {
            countingDownTimer.cancel();

            Button startButton = (Button) findViewById(R.id.start_button);
            startButton.setText("Resume Set " + setNumber);
            startButton.setVisibility(View.VISIBLE);

            Button stopButton = (Button) findViewById(R.id.stop_button);
            stopButton.setVisibility(View.INVISIBLE);
        }

    }

    public void resetTime(View v) {
        if (countingDownTimer != null) {
            countingDownTimer.cancel();

//            setTimeLeft();
            timeLeft = tempTime;

            TextView timerDisplay = (TextView) findViewById(R.id.timer_display_text_view);
            timerDisplay.setText("Timer: Press Start");

            Button startButton = (Button) findViewById(R.id.start_button);
            startButton.setText("Start Set " + setNumber);
            startButton.setVisibility(View.VISIBLE);

            Button stopButton = (Button) findViewById(R.id.stop_button);
            stopButton.setVisibility(View.INVISIBLE);
        }
    }

    public void endGameVisible() {
//        Button doneButton = (Button) findViewById(R.id.donePushUp_button);
//        doneButton.setVisibility(View.VISIBLE);

//        ImageView checkMarkImageView = (ImageView) findViewById(R.id.check_mark_image_view);
//        checkMarkImageView.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

//        ImageView checkMarkImageView = (ImageView) findViewById(R.id.check_mark_image_view);
//        builder.setView(checkMarkImageView);

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



//        View theThreeTimerButtons = findViewById(R.id.timer_buttons_linear_layout);
//        theThreeTimerButtons.setVisibility(View.INVISIBLE);
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
            Log.d("not integer", "error");
            Toast toast = Toast.makeText(this, "Enter an integer value.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 300);
            toast.show();
        }

        //update DB with new sets
        TextView exerciseName = (TextView) findViewById(R.id.exercise_name_text_view);
        MyDB.updateSets(exerciseName.getText().toString(), setEditText.getText().toString());

    }

    //triggered by clicking on 'Update TImer'. Updates the rest time limit, and stored in 'timeLeft' variable
    public void setTimeLeft(View v) {

        EditText TimeEditText = (EditText) findViewById(R.id.time_edit_text);

        try {
            timeLeft = Integer.parseInt(TimeEditText.getText().toString());
            Log.d("time: ", ""+timeLeft);
        }
        catch(NumberFormatException e){
            Log.d("not integer","error");
            Toast toast = Toast.makeText(this, "Enter an integer value.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 750);
            toast.show();
        }

        timeLeft = timeLeft * 1000;
        tempTime = timeLeft;

        //update DB with new time
        TextView exerciseName = (TextView) findViewById(R.id.exercise_name_text_view);
        MyDB.updateTimer(exerciseName.getText().toString(), TimeEditText.getText().toString());
        Log.d("stored time in DB", "yes");

    }

}