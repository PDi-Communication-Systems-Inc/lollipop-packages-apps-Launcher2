package com.android.launcher2;

import com.android.launcher.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import android.os.Process;
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

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.*;
import android.os.UserManager;
import android.os.UserHandle;
import android.os.Process;
import java.lang.reflect.Method;


public class PinOrPasswordChoice extends Activity {

    private RadioGroup radioPinPwdGroup;
    private RadioButton radioPinOrPwd;
    private Button btnContinue;
    protected static DevicePolicyManager mDPM;
    protected boolean mPasswordHasBeenSet = false;
    private static final String TAG = "PinOrPasswordChoice";
    protected static ComponentName mDeviceAdmin;

    private static final int REQUEST_CODE_RESET_PIN = 1;
    private static final int REQUEST_CODE_RESET_PASSWORD = 2;
    private static final long MS_PER_MINUTE = 60 * 1000;
    private static final long MS_PER_HOUR = 60 * MS_PER_MINUTE;
    private static final long MS_PER_DAY = 24 * MS_PER_HOUR;
    private static final String PatientsPinPwd = "patientsPinPwd.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_pin_password);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(this, DeviceAdminSampleReceiver.class);

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

        addListenerOnButton();

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
/*
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
*/

    @Override
    public void onBackPressed() {
    }

    /* saves pin or password setting from the owner account */
    private void savePinPwdSetting(String pinPwd) {
       BufferedWriter output = null;
       File dirLauncher = getObbDir();
       Log.i(TAG,"creating directory - "+ dirLauncher.getAbsolutePath());
       dirLauncher.mkdir();
       File file = new File(dirLauncher, PatientsPinPwd);

       try {
         Log.i(TAG,"creating file - "+ file.getAbsolutePath());
         file.createNewFile();
         output = new BufferedWriter(new FileWriter(file));
         output.write(pinPwd);
         Log.i(TAG,"Made the pin/pwd selection as - "+ pinPwd);
       } catch (IOException e) {
           e.printStackTrace();
           Log.e(TAG,"Error in writing launcher selection for patients ");
       } finally {
            try {
                if ( output != null ) {
                     output.close();
                }
            }  catch (IOException e) {
                  Log.e(TAG,"Error in closing the BufferedWriter");
            }
      }
    }

    /* Save the PIN choice for the Patient's account */
    private void choosePinForPatient() {
           savePinPwdSetting("PIN");
           finish();
    }

    /* Save the Password choice for the Patient's account */
    private void choosePwdForPatient() {
           savePinPwdSetting("password");
           finish();
    }

    public void addListenerOnButton() {

        radioPinPwdGroup = (RadioGroup) findViewById(R.id.choicePinPwd);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selectedId = radioPinPwdGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioPinOrPwd = (RadioButton) findViewById(selectedId);

                Toast.makeText(PinOrPasswordChoice.this,
                        radioPinOrPwd.getText(), Toast.LENGTH_SHORT).show();
                if(selectedId == R.id.radioPIN ) {
                   choosePinForPatient();

                } else if (selectedId == R.id.radioPWD ) {
                   choosePwdForPatient();
                }

            }

        });

    }

        private void ShowToast(String message, int duration)
        {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, message, duration);
                toast.setGravity(Gravity.BOTTOM|Gravity.CENTER, 0, 0);
                toast.show();
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

  protected void callRestrictedAppUserSetup() {
           // start the restricted user config activity

                   // TODO: Complete app restriction choices later on
           Intent intent = new Intent(this, AppChoiceView.class);
           startActivity(intent);
           return;
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

       // @Override
       // public CharSequence onDisableRequested(Context context, Intent intent) {
           // return context.getString(R.string.admin_receiver_status_disable_warning);
       // }

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
                                /*if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                                {                                                               
                                        try
                                        {                                                                               
                                        }
                                        catch (Exception e)
                                        {                                                                               
                                        }                               
                                } else{
                                    // do something for phones running an SDK before 4.2
                                }   */
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


