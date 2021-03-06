package com.android.launcher2;

import com.android.launcher.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import android.content.pm.PackageManager;
import android.content.pm.IPackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ActivityInfo;

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
import android.widget.CheckBox;

import android.os.SystemProperties;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.UserManager;
import android.os.UserHandle;
import android.os.Process;
import java.lang.reflect.Method;
import android.util.Log;
import android.os.UserHandle;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.List;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.*;

public class LauncherSelectionActivity extends Activity {


private RadioGroup chooseLauncherGroup;
private RadioButton selectedLauncher;
private Button btnContinue;
static final String TAG = "LauncherSelectionActivity";
private static final String LauncherInfo = "patientsLauncher.txt";



 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_launcher);
        addListenerOnButton();
}

private void saveLaunchersetting(String pkgName) {
 BufferedWriter output = null;
 File dirLauncher = getObbDir();
     Log.i(TAG,"creating directory - "+ dirLauncher.getAbsolutePath());
      dirLauncher.mkdir();
 File file = new File(dirLauncher, LauncherInfo);

  try {
     Log.i(TAG,"creating file - "+ file.getAbsolutePath());
     file.createNewFile();
     output = new BufferedWriter(new FileWriter(file));
     output.write(pkgName);
     Log.i(TAG,"Made the launcher selection as - "+ pkgName);
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


private void setThirdPartyLauncher() {
Log.i(TAG,"setThirdPartyLauncher() for patients ");
saveLaunchersetting("com.allentek.abe");
finish();
}

private void setDefaultLauncher() {
Log.i(TAG,"setDefaultLauncher() for patients ");
saveLaunchersetting("com.android.launcher");
finish();
}

    public void addListenerOnButton() {

        chooseLauncherGroup = (RadioGroup) findViewById(R.id.chooseLauncher);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
        Context context = getApplicationContext();
            @Override
            public void onClick(View v) {

                // get selected radio button from chooseLauncherGroup
                int selectedId = chooseLauncherGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                selectedLauncher = (RadioButton) findViewById(selectedId);

                //Toast.makeText(PinOrPasswordChoice.this,
                    //    radioPinOrPwd.getText(), Toast.LENGTH_SHORT).show();
                if(selectedId == R.id.defaultLauncher ) {
                   setDefaultLauncher();

                } else if (selectedId == R.id.thirdPartyLauncher ) {
                   setThirdPartyLauncher();
                }

            }

        });

    }
}
