
package com.android.launcher2;

//import android.app.AppGlobals;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.view.View;
import android.view.ViewParent;
import android.view.View.OnClickListener;
import android.view.Gravity;
import android.widget.AdapterView;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
//import android.content.pm.IPackageManager;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.android.launcher.R;
import com.android.launcher2.AppChoiceContract.AppChoiceEntry;

import android.util.Log;

public class AppChoiceView extends Activity {


   private ListView listView1;
   private static final String TAG = "AppChoiceView";
   private ArrayList<AppChoice> userAppsAccess = null;
   
   private class AppChoiceDbController extends AsyncTask<Context, Integer, Long> {

		@Override
		protected Long doInBackground(Context... params) {
	        /* Write permissions to database */
	        AppChoiceDbHelper mDbHelper = new AppChoiceDbHelper(params[0]);
	        
	        // Gets the data repository in write mode
	        SQLiteDatabase db = mDbHelper.getWritableDatabase();
	        
	        // Remove existing set of entries 
	        db.delete(AppChoiceEntry.TABLE_NAME, null, null);
	        
	        // Create a new map of values, where column names are the keys
	        ContentValues values = new ContentValues();
	        int appsProcessed = 0;
	        // Place data from model into database
	        for (AppChoice app : userAppsAccess) {
	          values.put(AppChoiceEntry.COLUMN_NAME_ID, appsProcessed++);
	      	  values.put(AppChoiceEntry.COLUMN_NAME_ICON, app.appIcon);
	      	  values.put(AppChoiceEntry.COLUMN_NAME_TITLE, app.appTitle);
	      	  values.put(AppChoiceEntry.COLUMN_NAME_ALLOW, app.allow); 
	      	  
	      	  long newRowId = 
	      			  db.insert(AppChoiceEntry.TABLE_NAME, "null", values);
	      	  Log.v(TAG, "Inserted row:"  + newRowId);
	      	  setProgress((int)((float)appsProcessed/userAppsAccess.size()));
	        }          
	        
	        // Release reference
	        db.close();
			return null;
		}
		
	    protected void onProgressUpdate(Integer... progress) {
	        setProgress(progress[0]);
	    }
	
	    protected void onPostExecute(Long result) {
	       ShowToast("Completed App Choice Setup for Restricted Users", 
	    		     Toast.LENGTH_SHORT);
	    }
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.new_user_app_restrictions);

      userAppsAccess = new ArrayList<AppChoice>();

      // test data
      UserManager um = (UserManager) getSystemService(USER_SERVICE);
      PackageManager pm = getPackageManager();
     // IPackageManager ipm = AppGlobals.getPackageManager();
      List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);
      Log.v(TAG, "User manager lists " + um.getUserCount() + " users on system");
      Log.v(TAG, "Current user is: " + um.getUserName());
      Log.v(TAG, "There are " + installedApps.size() + " apps installed " );
      for (ApplicationInfo app: installedApps) {  
    	  Log.v(TAG, "Add app: " + app.toString());
    	  userAppsAccess.add(new AppChoice(app.icon, app.packageName, true));
      }

      // the adapter has the layout for each item
      AppChoiceAdapter aca = new AppChoiceAdapter(this, 
    		  R.layout.listview_custom_item_row,
    		  userAppsAccess);

      ListView listView1 = (ListView)findViewById(R.id.listView1);

      // the header comes from header xml and gets inflated
      View header = (View)getLayoutInflater().inflate(
         R.layout.listview_custom_header_row, null);

      // attach header and items to listview
      listView1.addHeaderView(header);
      listView1.setAdapter(aca);

      // do something with item clicks
      listView1.setOnItemClickListener(new OnItemClickListener() {

      	 @Override
         public void onItemClick(AdapterView<?> parent, final View view,
            int position, long id) {
            Log.v(TAG, "Item " + position + " with id " + id + " was clicked");
         }
      });

      final Button button_continue = (Button)findViewById(R.id.button_continue);
      button_continue.setOnClickListener(btnContinueListener);
  
      final Button button_Cancel = (Button)findViewById(R.id.button_cancel);
      button_Cancel.setOnClickListener(btnCancelListener);
     
   }

   private OnClickListener btnCancelListener = new OnClickListener() {   

      public void onClick(View v) {   
         ShowToast("Skipping Administrative Setup", Toast.LENGTH_SHORT); 
         
         // Generate database with app choice settings for restricted users
         new AppChoiceView.AppChoiceDbController().execute(v.getContext());
         
         // go back to the previous activity
         finish();
         
         return;
      }   

   };  
    
   private OnClickListener btnContinueListener = new OnClickListener() {   

       public void onClick(View v) {   
          ShowToast("Finishing Administrative Setup", Toast.LENGTH_SHORT);
          
          // Generate database with app choice settings for restricted users
          new AppChoiceView.AppChoiceDbController().execute(v.getContext());

          // go back to the previous activity
          finish();
          
          return;
       }   
     
   };  
   
   public void onToggleClicked(View view) {
	    // Is the toggle on?
	    boolean isFlippedOn = ((Switch) view).isChecked();
	    int id = (Integer) ((Switch) view).getTag();
	    ViewParent parent = view.getParent();
	    
	    Log.v(TAG, ((Switch) view).toString() + 
	    		" was clicked and has tag " + id);
	    
	    // Write change in access for package to model
    	AppChoice nAppChoice = userAppsAccess.get(id);
    	Log.v(TAG, "AppChoice before change:" + nAppChoice.toString());
    	nAppChoice.setAllow(isFlippedOn);
    	Log.v(TAG, "AppChoice is now:" + nAppChoice.toString());
	}

   private void ShowToast(String message, int duration) {   
      Context context = getApplicationContext();
      Toast toast = Toast.makeText(context, message, duration);
      toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0); 
      toast.show();    
   }   

}
