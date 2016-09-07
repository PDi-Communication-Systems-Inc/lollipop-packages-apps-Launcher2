package com.android.launcher2;

import android.net.Uri;
import android.provider.BaseColumns;

public final class AppChoiceContract {
	
	/**
	 * To prevent someone from accidentally instantiating the cotnract class, 
	 * give it an empty constructor
	 */
	public AppChoiceContract(){}
	
	
	/* Inner class that defines the table contents */
	public static abstract class AppChoiceEntry implements BaseColumns {
		public static final String TABLE_NAME        = "app_permissions";
		public static final String COLUMN_NAME_ID  = "_id";
		public static final String COLUMN_NAME_ICON  = "appIcon";
		public static final String COLUMN_NAME_TITLE = "appTitle";
		public static final String COLUMN_NAME_ALLOW = "allow";
		public static final String SIMPLE_AUTHORITY = "com.android.launcher2.appchoicecontract";
		public static final String ALL_CHOICES = "appchoices";
		public static final Uri CONTENT_URI = Uri.parse("content://" + SIMPLE_AUTHORITY + "/" + ALL_CHOICES);
	}
	
}
