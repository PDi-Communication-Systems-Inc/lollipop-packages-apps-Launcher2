package com.android.launcher2;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.ToggleButton;
import android.widget.ListView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.ViewParent;
import android.app.Activity;

import java.util.ArrayList;
import java.lang.StringBuilder;

import com.android.launcher.R;

import android.util.Log;

public class AppChoiceAdapter extends ArrayAdapter<AppChoice> {

   Context context;
   int layoutResourceId;
   ArrayList<AppChoice> userAppsAccess = null; 
   private static final String TAG = "AppChoiceAdapter";
   PackageManager pm = null;
   
   static class AppChoiceHolder {
	  ImageView appImage;
      TextView appTitle;
      Switch appSwitch;
   }

   public AppChoiceAdapter(Context context, int layoutResourceId,
                            ArrayList<AppChoice> userAppsAccess) {
       super(context, layoutResourceId, userAppsAccess);
       this.context = context; 
       this.layoutResourceId = layoutResourceId;
       this.userAppsAccess = userAppsAccess;
       pm = context.getPackageManager();

       Log.v(TAG, this.toString());
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      View row = convertView;
      AppChoiceHolder oneAppHolder = null;

      if (row == null) {
	     LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	     Log.v(TAG, "Inflating using id:" + layoutResourceId);
         row = inflater.inflate(layoutResourceId, parent, false);

	     oneAppHolder = new AppChoiceHolder();
	     oneAppHolder.appImage = (ImageView)row.findViewById(R.id.appIcon);
         oneAppHolder.appTitle = (TextView)row.findViewById(R.id.appTitle); 
         oneAppHolder.appSwitch = (Switch)row.findViewById(R.id.appSwitch);

         row.setTag(oneAppHolder);

	     Log.v(TAG, "Row is null, setting row tag with new AppChoiceHolder" +
               " entry");
	      
	     oneAppHolder.appSwitch.setTag((Integer)position); 
	     Log.v(TAG, "Set tag on switch to: " + oneAppHolder.appSwitch.getTag());
      }
      else {
	     Log.v(TAG, "Acquiring Entry by getTag()");
         oneAppHolder = (AppChoiceHolder)row.getTag();
      }

      AppChoice yetAnotherApp = userAppsAccess.get(position);
      if (yetAnotherApp != null) {
    	  if (oneAppHolder != null) {
    		  
    		  String theTitle = yetAnotherApp.appTitle;    		  
    		  if (theTitle != null) {
    			  if (oneAppHolder.appTitle != null) {
    				  oneAppHolder.appTitle.setText(theTitle);
    				  Drawable appIcon = null;
    				  try {
    					  appIcon = pm.getApplicationIcon(yetAnotherApp.appTitle);
    				  }
    				  catch (NameNotFoundException mnfe) {
    					  Log.e(TAG, mnfe.toString());
    				  }
    				  if (appIcon != null) {
        				  oneAppHolder.appImage.setImageDrawable(appIcon);
        			  }
    			  }
    			  else {
    				   Log.e(TAG, "AppChoiceHolder TextView is null!");
    				   oneAppHolder.appTitle = new TextView(this.context);
    				   oneAppHolder.appTitle.setText(theTitle);
    			  }
    		  }
    		  
        	  Boolean allowIt = yetAnotherApp.allow;
        	  if (allowIt != null) {
        		  if (oneAppHolder.appSwitch != null) {
        			  oneAppHolder.appSwitch.setChecked(allowIt);
        		  }
        		  else {
        			  Log.e(TAG, "AppChoiceHolder Switch is null");
        			  oneAppHolder.appSwitch = new Switch(this.context);
        			  oneAppHolder.appSwitch.setChecked(allowIt);
        		  }
        	  }
        	  
        	  Integer icon = yetAnotherApp.appIcon;
        	  if (icon != null) {
        		  if (oneAppHolder.appImage != null) {
        			  Drawable appIcon = oneAppHolder.appImage.getDrawable();
        			 if (appIcon == null) {
        				 Log.w(TAG, "Image drawable not found, "
        				 		+ "usng image resource identifer");
        				 Resources r = null;
        				 try {
        				     r = pm.getResourcesForApplication(
        					  			 yetAnotherApp.appTitle);
        				 }
        				 catch (NameNotFoundException mnfe) {
        					 Log.e(TAG, mnfe.toString());
        				 }
        				 if (r != null) {
        					 Drawable iconFromApp = 
        							 r.getDrawable(yetAnotherApp.appIcon);
        					 oneAppHolder.appImage.setImageDrawable(
        							 iconFromApp);
        				 }
        			 }
        		  }
        		  else {
        			  Log.e(TAG, "AppChoiceHolder ImageView is null");
        			  oneAppHolder.appImage = new ImageView(context);
        		  }
        	  }
    	  }
    	  else {
    		  Log.e(TAG, "AppChoiceHolder not available");
    	  }
      }
      else {
    	  Log.e(TAG, "AppChoice data element not available");
      }
      
      return row;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("context=" + context + " layoutResourceID=" 
                + layoutResourceId + " userAppsAccess=");
      for (AppChoice app : userAppsAccess) {
         sb.append(app.toString() + ";"); 
      }
      return sb.toString();
   }
}

