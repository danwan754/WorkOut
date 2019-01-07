package com.example.dan.workout.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.dan.workout.R;
import com.example.dan.workout.helper.DBHelper;
import com.example.dan.workout.model.Exercise;

public class AddExerciseDialog {

    private Context context;
    private DBHelper MyDB;
    private AlertDialog alertDialog;

    public AddExerciseDialog(Context context) {
        this.context = context;
        this.MyDB = new DBHelper(context);
    }

    public void createAddExerciseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //set the new exercise layout in dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogueLayout = inflater.inflate(R.layout.dialog_add_exercise, null);
        builder.setView(dialogueLayout);

        final EditText name_editText = (EditText) dialogueLayout.findViewById(R.id.name_to_add_edit_text);

        final InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
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
                MyDB.insertExercise(new Exercise(name_editText.getText().toString()));
//                createButton(temp_name_holder);
                dialog.dismiss();
            }
        });

        alertDialog = dialog;
    }


    public void showDialog() {
        alertDialog.show();
    }
}
