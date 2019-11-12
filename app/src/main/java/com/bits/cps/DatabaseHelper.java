package com.bits.cps;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cps.db";
    private static final String TABLE_NAME = "location_data";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,User_id TEXT,Locality TEXT,date_time REAL)";
        db.execSQL(createTable);

        addData("2", "Phadke Road, Dombivli", "06-11-2019_01:26:22", db);
        addData("3", "Tilak Road, Dombivli", "06-11-2019_01:28:22", db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }

    public boolean addData(String User_id, String locality, String date_time, SQLiteDatabase sqLiteDatabase) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("User_id", User_id);
        contentValues.put("Locality", locality);
        contentValues.put("Date_time", date_time);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        return true;
    }

}
