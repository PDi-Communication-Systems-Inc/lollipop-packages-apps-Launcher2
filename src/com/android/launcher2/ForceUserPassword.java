package com.android.launcher2;


import java.lang.reflect.Method;

import com.android.launcher.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import android.os.UserManager;
import android.os.UserHandle;
import android.os.Process;

public class ForceUserPassword extends Activity {

    protected ForceUserPassword mActivity;
    protected static DevicePolicyManager mDPM;
    protected static ComponentName mDeviceAdmin;
    protected boolean mAdminActive = false;
    protected boolean mPasswordHasBeenSet = false;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private static final int REQUEST_CODE_RESET_PASSWORD = 2;
    
    private static final long MS_PER_MINUTE = 60 * 1000;
    private static final long MS_PER_HOUR = 60 * MS_PER_MINUTE;
    private static final long MS_PER_DAY = 24 * MS_PER_HOUR;
    
    private static final String TAG = "ForceUserPassword";
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {    	
    	Log.i(TAG, "Force user password's focus changed");
    	Log.i(TAG, mPasswordHasBeenSet ? "Password has been set" : 
    			                         "Password has not been set");
    	if(!mPasswordHasBeenSet)
        {
	    	//if password has not been set - keep the status bar collapsed
    		//TBD- Does not completely secure the status bar - change statusbarcode
	    	try
            {
               if(!hasFocus)
               {
                    Object service  = getSystemService("statusbar");
                    Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                    Method collapse = statusbarManager.getMethod("collapse");
                    collapse.setAccessible(true);
                    collapse.invoke(service);
                    Log.i(TAG, "Status Bar Collapsed");
        	    	
        	    	//and send home intent to bring this to the front again
                    //Intent startMain = new Intent(Intent.ACTION_MAIN);
                    //startMain.addCategory(Intent.CATEGORY_HOME);
                    //startActivity(startMain);
                    //Log.i(TAG, "HOME intent sent");
               }
            }
            catch(Exception ex)
            {
            	Log.e(TAG, "Exception caught on collpasing the status bar");
            }	    	
        }
    	else {
    		finish();
    	}
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
		
           requestWindowFeature(Window.FEATURE_NO_TITLE);
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
           // Prepare to work with the DPM
           mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
           mDeviceAdmin = new ComponentName(this, DeviceAdminSampleReceiver.class);
        
           //get data passed from the 
      	   Bundle extras = getIntent().getExtras();
      	   if(extras !=null) {
             String action = extras.getString("ACTION");
             String kind = extras.getString("KIND");	
      		    
             if(action.compareTo("ACTION") == 0)
             {
      	         try
      		 {
      		    DoAdminAction(kind);
      		 }
      		 catch (Exception e) {
      		    Log.e(TAG, e.getMessage() );
      	         }		
             }	    		   
           }	
        
	   setContentView(R.layout.force_user_password);
		
	   // Capture our button from layout
	   final Button button = (Button)findViewById(R.id.button1);
	   button.setOnClickListener(btnListener);
	   button.requestFocus();
	
	   checkAndReturnResult();				
		   
	}
	
	private void DoAdminAction(String kind)
	{
		//Do the Admin Action
		try 
		{     			
			if (mDPM.isAdminActive(mDeviceAdmin)) 
			{
				if(kind.compareTo("LOCK") == 0)
				{
					mDPM.lockNow();
				}				 
			}
			else
				Log.e(TAG, "Device admin not active- cannot execute action" );
							
		} 
		catch (Exception e) {
			Log.e(TAG, e.getMessage() );
		}		
	}
	
	private void ShowToast(String message, int duration)
	{
		Context context = getApplicationContext();
		Toast toast = Toast.makeText(context, message, duration);
		toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
		toast.show();			
	}
	
	private OnClickListener btnListener = new OnClickListener()
	{

	    public void onClick(View v)
	    {   
	    	if (!mDPM.isAdminActive(mDeviceAdmin)) 
			{			
				try
				{
					// try to become active - must happen here in this activity, to get result
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,	mDeviceAdmin);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This activation is Required to securely delete your data after discharge!");
					startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);				
				} 
				catch(Exception e)
				{
					Log.e(TAG, e.getMessage() );
					ShowToast("Excpetion caught." + e.getMessage() , Toast.LENGTH_SHORT);								
				}
			}			
			else {
				// Already is a device administrator, can do security operations now.
				setAdminRules();
				resetPassword();						
			}		
	    } 

	}; 
	
	//TODO: Find out why this did not work for the user builds
	public void HandleOnClick(View view) 
	{
		if (!mDPM.isAdminActive(mDeviceAdmin)) 
		{			
			try
			{
				// try to become active - must happen here in this activity, to get result
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,	mDeviceAdmin);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This activation is Required to securely delete your data after discharge!");
				startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);				
			} 
			catch(Exception e)
			{
				Log.e(TAG, e.getMessage() );
				ShowToast("Excpetion caught." + e.getMessage() , Toast.LENGTH_SHORT);								
			}
		}			
		else {
			// Already is a device administrator, can do security operations now.
			setAdminRules();
			resetPassword();						
		}			
		
	}
	
	private void setAdminRules()
	{
		try
		{
			//TODO: Make these configurable
			mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
			//mDPM.setPasswordQuality(mDeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
			mDPM.setPasswordMinimumLength(mDeviceAdmin, 4); //changed from 6 to 4 - NIH feedback
			mDPM.setMaximumTimeToLock(mDeviceAdmin, 30 * MS_PER_MINUTE); //maximum lock time (after sleep) that can be set by the user
			//mDPM.setMaximumFailedPasswordsForWipe(mDeviceAdmin, 6);	Do not factory reset device on (any number of) wrong pin entries	
		}
		catch(Exception e)
		{
			Log.e(TAG, e.getMessage() );								
		}
	}
	
	private void resetPassword()
	{
		Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
		//use internal API - LockPatternUtils.PASSWORD_TYPE_KEY = "lockscreen.password_type";
		//invoke the PIN - note that this will eventually depend on the quality of password set by admin
		intent.putExtra("lockscreen.password_type", DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);
		startActivityForResult(intent, REQUEST_CODE_RESET_PASSWORD);		
	}
	
	private void showOKDialog(String message,String title)
	{
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setMessage(message).setTitle(title);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	           }
	       });
		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    //TODO handle here. 
	    
	    if (REQUEST_CODE_ENABLE_ADMIN == requestCode)
	    {
		    if (resultCode == Activity.RESULT_OK) {
		    	// Has become the device administrator.
		    	ShowToast("Granted Admin",Toast.LENGTH_SHORT);
				
				setAdminRules();				
				resetPassword(); 
				//showOKDialog("Please choose your password","Password Type");
	            
		    } else {
		    	//Canceled or failed.
		    	ShowToast("Failed or cancelled Admin",Toast.LENGTH_SHORT);		    	
		    	ShowToast( "Device Admin must be Activated to continue!",Toast.LENGTH_SHORT);    			    					
		    }
	    }
	    if (REQUEST_CODE_RESET_PASSWORD == requestCode)
	    {
		    if (resultCode == Activity.RESULT_OK) 
		    {
		    	if(!mDPM.isActivePasswordSufficient())
				{				
		    		showOKDialog("Please choose your password","Insufficient Password");					
					resetPassword();
				}
				else // password is sufficient					
				{
					mPasswordHasBeenSet= true;					
					ShowToast( "Password Successfully Changed",Toast.LENGTH_SHORT);
				}	                        
		    }
		    else // if user chose cancelled from password change 
		    {
		    	//check again for sufficiency
				if(!mDPM.isActivePasswordSufficient())
				{	
					ShowToast( "Password not sufficient",Toast.LENGTH_SHORT);
			    	resetPassword();
				}
				else // password is sufficient					
				{
					mPasswordHasBeenSet= true;
					ShowToast( "Password conforms to password policy",Toast.LENGTH_SHORT);
				}
		    }
	    }	    
	    UserHandle uh = Process.myUserHandle();
    	UserManager um = (UserManager) getSystemService(Context.USER_SERVICE);
   	    if(null != um) {
              long userSerialNumber = um.getSerialNumberForUser(uh);
              Log.d(TAG, "userSerialNumber = " + userSerialNumber);
              if ((userSerialNumber == 0) && (mPasswordHasBeenSet)) {
                 callRestrictedAppUserSetup();
              } else {
            	  Log.d(TAG, "Not the admin, not calling callRestrictedAppUserSetup()");
              } 
   	    
	    }
	    checkAndReturnResult();
	    return;
	}

	protected void callRestrictedAppUserSetup() {
           // start the restricted user config activity
			
		   // TODO: Complete app restriction choices later on
         //  Intent intent = new Intent(this, AppChoiceView.class);
          // startActivity(intent);
           return;
    }
	
	protected void checkAndReturnResult()
	{
		//Verify password has been set
    	Log.i(TAG, mPasswordHasBeenSet ? "Password has been set" : 
                "Password has not been set");
		if(mPasswordHasBeenSet)
		{
			Intent data = this.getIntent();
			data.putExtra("PASSWORD", "SET");
			
			if (getParent() == null) {
			    setResult(Activity.RESULT_OK, data);
			} else {
			    getParent().setResult(Activity.RESULT_OK, data);
			}
			finish();
		}
		return;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	
    /**
     * Sample implementation of a DeviceAdminReceiver.  Your controller must provide one,
     * although you may or may not implement all of the methods shown here.
     *
     * All callbacks are on the UI thread and your implementations should not engage in any
     * blocking operations, including disk I/O.
     */
    public static class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
        void showToast(Context context, String msg) {
           // String status = context.getString(R.string.admin_receiver_status, msg);
           // Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {
            //showToast(context,  "Rahul says enabled");
        }
        @Override
        public void onDisabled(Context context, Intent intent) {
          // showToast(context, "Rahul says disabled");
        }

        @Override
        public void onPasswordChanged(Context context, Intent intent) {
           // showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
        }

        @Override
        public void onPasswordFailed(Context context, Intent intent) {
        	
	    	try
	    	{
	    		int count = mDPM.getCurrentFailedPasswordAttempts();	    		
	    		if (count >= (mDPM.getMaximumFailedPasswordsForWipe(mDeviceAdmin) - 1))
	    		{
	    			//delete this user in 4.2 and above	    			
	    			int currentapiVersion = android.os.Build.VERSION.SDK_INT; 			
	    		}
	    		else //count less than min - do nothing
	    		{
	    			//wipe data in 4.0.3
	    			//mDPM.wipeData(mDPM.WIPE_EXTERNAL_STORAGE);	    			
	    		}
	    	}
	    	catch(Exception e)
			{
	    		if ((e != null) && (e.getMessage() != null))
            		Log.e(TAG, e.getMessage()); 					
			}	    	
        }

        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {
           // showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
        }

        @Override
        public void onPasswordExpiring(Context context, Intent intent) {
        	
        }
    }
}
