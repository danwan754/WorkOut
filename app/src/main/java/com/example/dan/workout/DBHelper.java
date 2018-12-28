package com.example.dan.workout;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dan on 2016-05-06.

 * This database helper class was constructed following a guide: http://hmkcode.com/android-simple-sqlite-database-tutorial/
 **/
public class DBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "MyDB.db";
    public static final String TABLE_NAME = "exercises";
    public static final String COLUMN_EXERCISE = "exercise";
    public static final String COLUMN_SETS = "sets";
    public static final String COLUMN_REPS = "reps";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DONE = "done"; //'done' exercise?
    public static final int DATABASE_VERSION = 1;


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE exercises" + "(ID INTEGER PRIMARY KEY, exercise TEXT, sets TEXT, reps TEXT, time TEXT, done TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS exercises");
        onCreate(db);
    }

//    public boolean insertExercise(String exercise, String sets, String reps, String time, String done){
    public boolean insertExercise(String exercise){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EXERCISE, exercise);
//        contentValues.put(COLUMN_SETS, sets);
//        contentValues.put(COLUMN_REPS, reps);
//        contentValues.put(COLUMN_TIME, time);
//        contentValues.put(COLUMN_DONE, done); //note that 'done' is 'yes' or 'no'
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public void updateSets(String exercise_name, String sets ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(COLUMN_SETS, sets);
        db.update(TABLE_NAME, updateValues, COLUMN_EXERCISE+"=?", new String[]{exercise_name});
    }

    public void updateTimer(String exercise_name, String time ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(COLUMN_TIME, time);
        db.update(TABLE_NAME, updateValues, COLUMN_EXERCISE+"=?", new String[]{exercise_name});
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

//        Log.d("after rawQuery: ","appear?");

        for(int i=0; i< column_list.length; i++) {

            field_data.add(cursor.getString(cursor.getColumnIndexOrThrow(column_list[i])));
//          cursor.moveToNext();
        }

//        Log.d("should be 3 columns: ",column_list[0]+","+column_list[1]+","+column_list[2]);
        return field_data;
    }

    public ArrayList<String> getAllExercises() {
        ArrayList<String> exercise_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM exercises", null);
        cursor.moveToFirst();

        //while end of query not reached
        while (cursor.isAfterLast() == false) {
            exercise_list.add(cursor.getString(cursor.getColumnIndex(COLUMN_EXERCISE)));
            cursor.moveToNext();
        }
//        Log.d("list all exercises", ""+ exercise_list.toString());
        return exercise_list;
    }

    public void deleteExercise(String exercise_name ){

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_EXERCISE+"=?", new String[]{String.valueOf(exercise_name)});
 //       Log.d("")
    }
}
