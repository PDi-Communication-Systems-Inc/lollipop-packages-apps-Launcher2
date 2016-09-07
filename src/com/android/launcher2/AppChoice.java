package com.android.launcher2;

import java.lang.StringBuilder;
import android.util.Log;

public class AppChoice {
   private static final String TAG = "AppChoice";
   public int appIcon;     // icon for package
   public String appTitle; // package name
   public Boolean allow;   // allow to use package
   
   public AppChoice(int icon, String appTitle, Boolean allow) {
      super();
      this.appIcon = icon;
      this.appTitle = appTitle;
      this.allow = allow;
      Log.d(TAG, toString());
   }

   public AppChoice(int appIcon, String appTitle) {
      super();
      this.appIcon = appIcon;
      this.appTitle = appTitle;
      this.allow = true;
      Log.d(TAG, toString());
   }

   public Boolean getAllow() {
	   return allow;
	}
	
	public void setAllow(Boolean allow) {
		this.allow = allow;
	}
	
	public int getAppIcon() {
		return appIcon;
	}
	
	public String getAppTitle() {
		return appTitle;
	}

@Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append( "appIcon: " + appIcon + 
                 " appTitle: " + appTitle + 
 		 " allow: " + allow);
      return sb.toString();
   }
}
