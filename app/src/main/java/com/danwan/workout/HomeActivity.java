package com.danwan.workout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.danwan.workout.dialog.SettingsDialog;
import com.danwan.workout.helper.DBHelper;
import com.danwan.workout.model.Exercise;

import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {
    DBHelper MyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        MyDB = new DBHelper(this);

        //fetch all exercises in database
        ArrayList exercise_list = MyDB.getAllExerciseNames();

        //create button for each exercise in DB
        if (exercise_list.size() != 0) {
            for (int i = 0; i < exercise_list.size(); i++) {
                createButton(exercise_list.get(i).toString());
            }
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

    //called when user clicks on an exercise button
    public void startExercise(View view) {
        Intent startExerciseNow = new Intent(this, MainActivity.class);
        Button b = (Button) view;
        startExerciseNow.putExtra("exerciseName", b.getText().toString());
        startActivity(startExerciseNow);
    }

    //adds a button for an exercise in activity_home layout, and insert exercise in DB
    public void addExerciseList(View view) {

////////////////////////// use the logic in createAddExerciseDialog method in AddExerciseDialog class
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //set the new exercise layout in dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogueLayout = inflater.inflate(R.layout.dialog_add_exercise, null);
        builder.setView(dialogueLayout);

        final EditText name_editText = dialogueLayout.findViewById(R.id.name_to_add_edit_text);

        final InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

        final AlertDialog dialog = builder.show();

        // hide the default rectangular container of the dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // button closes settings dialog on click
        Button cancelButton = dialogueLayout.findViewById(R.id.add_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(name_editText.getWindowToken(), 0);
                dialog.cancel();
            }
        });

        // button saves settings dialog on click
        Button saveButton = dialogueLayout.findViewById(R.id.add_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseName = name_editText.getText().toString();

                // verify that name is not empty and does not already exist
                if (exerciseName.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if (MyDB.getAllExerciseNames().contains(exerciseName)) {
                    Toast.makeText(HomeActivity.this, "Exercise '" + exerciseName + "' already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    MyDB.insertExercise(new Exercise(exerciseName));
                    createButton(exerciseName);

                    //hide the soft keyboard
                    imm.hideSoftInputFromWindow(name_editText.getWindowToken(), 0);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void removeExercise(View view){
        final ArrayList removal_list = MyDB.getAllExerciseNames();
        final ArrayList<Integer> temp_list = new ArrayList<>();
        String removal_list_string[] = new String[removal_list.size()];

        if(removal_list.size() == 0){
            return;
        }

        for(int i =0; i<removal_list.size();i++){
            removal_list_string[i] = (removal_list.get(i).toString());
        }

        //create dialog to allow user to select the exercises to delete
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.dialogRoundCorners);
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
    public void createButton(String s) {

        //this is the layout view in activity_home.xml where button will go
        LinearLayout listLayout = findViewById(R.id.exercise_index_layout);

        //create a button view from an xml and adds it into the layout view
        LayoutInflater inflater1 = LayoutInflater.from(this);
        final Button newButton = (Button) inflater1.inflate(R.layout.add_button_style, listLayout, false);
        listLayout.addView(newButton);

        if (!s.isEmpty()) {
            newButton.setText(s);
        }

        //clicking button will change from HomeActivity to MainActivity
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startExerciseNow = new Intent(HomeActivity.this, MainActivity.class);
                startExerciseNow.putExtra("exerciseName", newButton.getText().toString());
                startActivity(startExerciseNow);
            }
        });
    }
}