package com.example.dan.workout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class HomeActivity extends AppCompatActivity {
    String temp_name_holder;
 //   Boolean deleteMode =false;
    DBHelper MyDB;
 //   String temp_name_holder = null; //stores the name of new exercise added to list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("before database", "test");
        MyDB = new DBHelper(this);

        //fetch all exercises in database
        ArrayList exercise_list = MyDB.getAllExercises();

        Log.d("list size: ", "" + exercise_list.size());

        //create button for each exercise in DB
        if (exercise_list.size() != 0) {
            for (int i = 0; i < exercise_list.size(); i++) {

                createButton(exercise_list.get(i).toString());
            }
        }

    }

    //called when user clicks on an exercise button
    public void startExercise(View view) {
        Intent startExerciseNow = new Intent(this, MainActivity.class);
        Button b = (Button) view;
        startExerciseNow.putExtra("exerciseName", b.getText().toString());
        startActivity(startExerciseNow);
    }

    //adds a button for an exercise in activity_home layout, and insert exercise in DB
    public void addExerciseList(View view) {

        //create alert dialogue to prompt user for name of the exercise to add to list
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Name Exercise");

        //set an edittext view in dialogue box
        final EditText exercise_nom = new EditText(this);
        builder.setView(exercise_nom);

        //force the soft keyboard to show
        exercise_nom.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);


        builder.setPositiveButton(R.string.confirm_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                EditText exercise_nom = (EditText) findViewById(R.id.name_to_add_edit_text);
                temp_name_holder = exercise_nom.getText().toString();

                createButton(temp_name_holder);
                MyDB.insertExercise(temp_name_holder, "0", "0", "0", "no");
                Log.d("Exercise added:", temp_name_holder );

                //hide the soft keyboard
                imm.hideSoftInputFromWindow(exercise_nom.getWindowToken(), 0);

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //hide the soft keyboard
                imm.hideSoftInputFromWindow(exercise_nom.getWindowToken(), 0);

                dialog.cancel();
            }

        });
        builder.show();

    }

    public void removeExercise(View view){

        final ArrayList removal_list = MyDB.getAllExercises();
        final ArrayList<Integer> temp_list = new ArrayList<>();

        String removal_list_string[] = new String[removal_list.size()];

        if(removal_list.size() == 0){
            return;
        }

        for(int i =0; i<removal_list.size();i++){

            removal_list_string[i] = (removal_list.get(i).toString());
        }

        //create dialog to allow user to select the exercises to delete
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the exercises to delete");
        builder.setMultiChoiceItems(removal_list_string, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked){
                    temp_list.add(which);
                }
                else if(temp_list.contains(which)){
                    temp_list.remove(Integer.valueOf(which));
                }
            }
        });
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i =0; i<temp_list.size(); i++){
  //                  int j =  temp_list.get(i);
                    MyDB.deleteExercise(removal_list.get(temp_list.get(i)).toString());
                }

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();


    }


    //creates the button view to be displayed on list of exercises; returns the exercise name
    public String createButton(String s) {

        //this is the layout view in activity_home.xml where button will go
        LinearLayout listLayout = (LinearLayout) findViewById(R.id.exercise_index_layout);

        //create a button view from an xml and adds it into the layout view
        LayoutInflater inflater1 = LayoutInflater.from(this);
        final Button newButton = (Button) inflater1.inflate(R.layout.add_button_style, listLayout, false);
        listLayout.addView(newButton);

        if (s != null) {
            newButton.setText(s);
            Log.d("Read name: ", s);
        }

        //clicking button will change from HomeActivity to MainActivity
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

 //               Button b = (Button) newButton;

                Intent startExerciseNow = new Intent(HomeActivity.this, MainActivity.class);
                startExerciseNow.putExtra("exerciseName", newButton.getText().toString());
                startActivity(startExerciseNow);
            }
        });
        return newButton.getText().toString();
    }


}