package com.danwan.workout.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.danwan.workout.model.Exercise;
import com.danwan.workout.model.Settings;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper{

    // database name and version
    private static final String DATABASE_NAME = "MyDB";
    private static final int DATABASE_VERSION = 1;

    // table names
    private static final String EXERCISE_TABLE = "exercises";
    private static final String SETTINGS_TABLE = "settings";

    // fields of exercise table
    private static final String COLUMN_NAME = "exercise";
    private static final String COLUMN_SETS = "sets";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_REPS = "reps";
//    private static final String COLUMN_DONE = "done"; //'done' exercise?

    // fields of settings table
    private static final String COLUMN_VIBRATE = "vibrate";
    private static final String COLUMN_VOLUME = "volume";



    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE exercises" + "(ID INTEGER PRIMARY KEY, exercise TEXT, sets TEXT, reps TEXT, time TEXT, done TEXT)");
        db.execSQL("CREATE TABLE settings" + " (ID INTEGER PRIMARY KEY, vibrate TEXT, volume TEXT)");
        initialSettingsTableInsert(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS exercises");
        db.execSQL("DROP TABLE IF EXISTS settings");
        onCreate(db);
    }

    /* ----- "exercise" table methods ----- */

//    public boolean insertExercise(String exercise, String sets, String reps, String time, String done){
    public boolean insertExercise(Exercise exercise) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, exercise.getName());
//        contentValues.put(COLUMN_SETS, sets);
//        contentValues.put(COLUMN_REPS, reps);
//        contentValues.put(COLUMN_TIME, time);
//        contentValues.put(COLUMN_DONE, done); //note that 'done' is 'yes' or 'no'
        db.insert(EXERCISE_TABLE, null, contentValues);
        return true;
    }

    public int updateSets(String exercise_name, String sets ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(COLUMN_SETS, sets);
        return db.update(EXERCISE_TABLE, updateValues, COLUMN_NAME+"=?", new String[]{exercise_name});
    }

    public int updateTimer(String exercise_name, String time ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(COLUMN_TIME, time);
        return db.update(EXERCISE_TABLE, updateValues, COLUMN_NAME+"=?", new String[]{exercise_name});
    }
/*
    public String getSets(String exercise_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT sets FROM exercises WHERE exercise="+exercise_name, null);
        Log.d("cursor values: ",""+cursor);
        cursor.moveToFirst();
        String numSets = cursor.toString();
 //       String numSets = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SETS));

        Log.d("number of sets: ",""+numSets);

        return numSets;
    }

    public String getTime(String exercise_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT sets FROM exercises WHERE exercise="+exercise_name, null);
        cursor.moveToFirst();
        String timer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));

        Log.d("getTime: ",""+timer);

        return timer;
    }
*/
    public ArrayList<String> getFieldData(String exercise_name){
        ArrayList<String> field_data = new ArrayList<>();
        String column_list[] = new String[]{COLUMN_SETS, COLUMN_REPS, COLUMN_TIME};
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM exercises WHERE exercise= '"+exercise_name+"'", null);
        cursor.moveToFirst();

        for(int i=0; i< column_list.length; i++) {

            field_data.add(cursor.getString(cursor.getColumnIndexOrThrow(column_list[i])));
//          cursor.moveToNext();
        }

//        Log.d("should be 3 columns: ",column_list[0]+","+column_list[1]+","+column_list[2]);
        return field_data;
    }

    public ArrayList<String> getAllExerciseNames() {
        ArrayList<String> exercise_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM exercises", null);
        cursor.moveToFirst();

        //while end of query not reached
        while (!cursor.isAfterLast()) {
            exercise_list.add(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            cursor.moveToNext();
        }
//        Log.d("list all exercises", ""+ exercise_list.toString());
        return exercise_list;
    }

    public void deleteExercise(String exercise_name){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EXERCISE_TABLE, COLUMN_NAME+"=?", new String[]{String.valueOf(exercise_name)});
    }


    /* ----- "settings" table methods ----- */

    // update the single row in the settings table
    public void updateSettings(Settings settings) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String isVibrate = settings.isVibrate().toString();
        int volume = settings.getVolume();

        String updateSettingsQuery = "UPDATE " + SETTINGS_TABLE + " SET " + COLUMN_VIBRATE + "='"
                + isVibrate + "', " + COLUMN_VOLUME + "=" + volume + " WHERE id=1";

        Log.d("UpdateSettingsQuery", updateSettingsQuery);
        db.execSQL(updateSettingsQuery);
    }

    // returns the single row of the settings table
    public Settings getSettings() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM settings", null);
        cursor.moveToFirst();

        Settings settings = new Settings();
        Boolean isVibrate = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(COLUMN_VIBRATE)));
        int volume = cursor.getInt(cursor.getColumnIndex(COLUMN_VOLUME));
        settings.setVibrate(isVibrate);
        settings.setVolume(volume);

        return settings;
    }

    // insert the default one and only row to be in the settings table
    public long initialSettingsTableInsert(SQLiteDatabase db) {
        int volume = 0;
        String isVibrate = "false";

        ContentValues values = new ContentValues();
        values.put(COLUMN_VIBRATE, isVibrate);
        values.put(COLUMN_VOLUME, volume);

        long id = db.insert(SETTINGS_TABLE, null, values);
        return id;
    }
}
