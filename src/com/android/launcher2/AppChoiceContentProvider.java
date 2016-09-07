package com.android.launcher2;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.android.launcher2.AppChoiceContract.AppChoiceEntry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppChoiceContentProvider extends ContentProvider {
    private static final int DATABASE_VERSION = AppChoiceDbHelper.DATABASE_VERSION;
    private static final String DATABASE_NAME = AppChoiceDbHelper.DATABASE_NAME;
    private static final String TABLE_NAME = AppChoiceEntry.TABLE_NAME;
    
    private static final String ALLOW_COLUMN = AppChoiceEntry.COLUMN_NAME_ALLOW;
    private static final String ICON_COLUMN = AppChoiceEntry.COLUMN_NAME_ICON;
    private static final String TITLE_COLUMN = AppChoiceEntry.COLUMN_NAME_TITLE;
    private static final String ID_COLUMN = AppChoiceEntry.COLUMN_NAME_ID;
    
    private static final String APP_PER_QUERY = "select " + TITLE_COLUMN + 
    		" from " + TABLE_NAME;
    
    private AppChoiceDbHelper mChoiceHelper = null;
    private static UriMatcher sUriMatcher = null;
    private static Map<String, String> sAppChoiceProjectMap = null;    
    private static final String ALL_CHOICES = AppChoiceEntry.ALL_CHOICES;
    private static final int ALL_CHOICES_SUCCESS = 45066;
    
    private static SQLiteDatabase db = null;
    
    static {
    	sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    	sUriMatcher.addURI(AppChoiceEntry.SIMPLE_AUTHORITY, 
    			           AppChoiceEntry.TABLE_NAME + "/" + ALL_CHOICES, 
    			           ALL_CHOICES_SUCCESS);
    	
    	// if used, must map all columns from db
    	sAppChoiceProjectMap = new HashMap<String, String>();
    	sAppChoiceProjectMap.put(ID_COLUMN, 
				 				 AppChoiceEntry.COLUMN_NAME_ID);
    	sAppChoiceProjectMap.put(ALLOW_COLUMN, 
    							 AppChoiceEntry.COLUMN_NAME_ALLOW);
    	sAppChoiceProjectMap.put(ICON_COLUMN, AppChoiceEntry.COLUMN_NAME_ICON);
    	sAppChoiceProjectMap.put(TITLE_COLUMN, AppChoiceEntry.COLUMN_NAME_TITLE);
    }
	@Override
	public boolean onCreate() {
		// Check to see if database for app choices exist
		File database = this.getContext().getDatabasePath(DATABASE_NAME);
		if (!database.exists()) {
			// Database does not exist, so create it here
			AppChoiceDbHelper acdbh = 
					new AppChoiceDbHelper(this.getContext());
			db = acdbh.getWritableDatabase();
		}
		else {
			// Database exists, just open it
			String myPath = database.getAbsolutePath();
			db = SQLiteDatabase.openDatabase(myPath, null, 
					SQLiteDatabase.OPEN_READWRITE);
		}
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		int match = sUriMatcher.match(uri);
		
		Cursor c = null;
		
		switch(match) {
		case ALL_CHOICES_SUCCESS:
			c = db.query(TABLE_NAME, projection, selection, selectionArgs,
					     null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("unsupported uri: " + uri);
		}
		
		return c;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch(sUriMatcher.match(uri)) {
			case ALL_CHOICES_SUCCESS:				
				return "vnd.android.cursor.dir/com.android.launcher2";
			default:
				throw new IllegalArgumentException("unsupported uri:" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
