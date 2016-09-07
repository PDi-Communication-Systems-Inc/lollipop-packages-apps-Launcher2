package com.android.launcher2;

import com.android.launcher2.AppChoiceContract.AppChoiceEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AppChoiceDbHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =     
		   "CREATE TABLE " + AppChoiceEntry.TABLE_NAME + " (" +
		    AppChoiceEntry.COLUMN_NAME_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
		    AppChoiceEntry.COLUMN_NAME_ICON + INTEGER_TYPE + COMMA_SEP +
		    AppChoiceEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
		    AppChoiceEntry.COLUMN_NAME_ALLOW + INTEGER_TYPE  + ")";
    private static final String SQL_DELETE_ENTRIES = 
		   "DROP TABLE IF EXISTS " + AppChoiceEntry.TABLE_NAME;
	   
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AppChoicePermissions.db";

    public AppChoiceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
}
