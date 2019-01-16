package com.danwan.workout.dialog;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.danwan.workout.R;
import com.danwan.workout.helper.DBHelper;
import com.danwan.workout.model.Settings;

public class SettingsDialog {

    private Context context;
    private AlertDialog dialog;
    private DBHelper myDB;

    // only display the settings dialog if it is not currently displayed
    private static boolean isDisplayed = false;

    public SettingsDialog(Context context) {
        this.context = context;
        this.myDB = new DBHelper(context);
        createSettingsAlertDialog();
    }

    private void createSettingsAlertDialog() {

        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);

        //set the settings layout in dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogueLayout = inflater.inflate(R.layout.dialog_settings, null);
        builder.setView(dialogueLayout);

        final AlertDialog dialog = builder.show();

        // hide the default rectangular container of the dialog
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // get the vibrate switch view, volume seekBar, and progress textView
        final Switch vibrateSwitch = dialogueLayout.findViewById(R.id.settings_vibrate_switch);
        final SeekBar seekBar = dialogueLayout.findViewById(R.id.settings_volume_seekBar);
        final TextView progressTextView = dialogueLayout.findViewById(R.id.settings_progress_textView);

        Settings dbSettings = myDB.getSettings();
        Log.d("dbSettingsVolume", Integer.toString(dbSettings.getVolume()));

        // set settings in views
        vibrateSwitch.setChecked(dbSettings.isVibrate());
        seekBar.setProgress(dbSettings.getVolume());
        progressTextView.setText(Integer.toString(dbSettings.getVolume()));

        // listen for changes to progress in the seekbar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                progressTextView.setText(Integer.toString(progressChangedValue));
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // button closes settings dialog on click
        Button settingsBackButton = dialogueLayout.findViewById(R.id.settings_back_button);
        settingsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = new Settings();
                settings.setVibrate(vibrateSwitch.isChecked());
                settings.setVolume(Integer.parseInt(progressTextView.getText().toString()));
                myDB.updateSettings(settings);
                dialog.dismiss();
//                isDisplayed = false;
//                Log.d("isDisplayed", "gone now");
//                Log.d("isDisplayed", "value after close: " + Boolean.toString(isDisplayed));
            }
        });



        this.dialog = dialog;
    }

    public void showSettingsDialog() {

//        if (dialog.isShowing()) {
//            return;
//        }
//        if (isDisplayed) {
//            Log.d("isDisplayed", "already displayed");
//            Log.d("isDisplayed", "value: " + Boolean.toString(isDisplayed));
//            return;
//        }
//        Log.d("isDisplayed", "value before true: " + Boolean.toString(isDisplayed));
//        isDisplayed = true;
//        Log.d("isDisplayed", "now displayed");
//        Log.d("isDisplayed", "value after true: " + Boolean.toString(isDisplayed));

//        createSettingsAlertDialog();
        dialog.show();
    }
}
