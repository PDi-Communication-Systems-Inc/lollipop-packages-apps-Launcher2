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

import android.os.UserManager;
import android.os.UserHandle;
import android.os.Process;
import java.lang.reflect.Method;
import android.util.Log;
import android.os.UserHandle;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.List;

public class LauncherSelectionActivity extends Activity {


private RadioGroup chooseLauncherGroup;
private RadioButton selectedLauncher;
private Button btnContinue;

 static final String TAG = "LauncherSelectionActivity";
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_launcher);
        addListenerOnButton();
}


private static ComponentName[] getActivitiesListByActionAndCategory (Context context, String action, String category) {
   Intent queryIntent = new Intent(action);
   queryIntent.addCategory(category);
   List<ResolveInfo> resInfos = context.getPackageManager().queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY);
   ComponentName[] componentNames = new ComponentName[resInfos.size()];
   for (int i = 0; i < resInfos.size(); i++) {
      ActivityInfo activityInfo = resInfos.get(i).activityInfo;
      componentNames[i] = new ComponentName(activityInfo.packageName, activityInfo.name); }
   return componentNames; 
}


private void setThirdPartyLauncher(Context context) {
Log.d(TAG, "SAGAR setThirdPartyLauncher()");
context.getPackageManager().clearPackagePreferredActivities("com.android.launcher");
IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
   filter.addCategory(Intent.CATEGORY_HOME);
   filter.addCategory(Intent.CATEGORY_DEFAULT);
   ComponentName[] currentHomeActivities = getActivitiesListByActionAndCategory(context, Intent.ACTION_MAIN, Intent.CATEGORY_HOME);
   ComponentName newPreferredActivity = new ComponentName("com.teslacoilsw.launcher", "com.teslacoilsw.launcher.NovaLauncher");
   context.getPackageManager().addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, currentHomeActivities, newPreferredActivity);

 Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.teslacoilsw.launcher");
 launchIntent.setClassName("com.teslacoilsw.launcher", "com.teslacoilsw.launcher.NovaLauncher"); 
 launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
           // startActivity(launchIntent);
context.startActivityAsUser(launchIntent, UserHandle.CURRENT);
}


private void setDefaultLauncher(Context context) {
   context.getPackageManager().clearPackagePreferredActivities("com.teslacoilsw.launcher");

   IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
   filter.addCategory(Intent.CATEGORY_HOME);
   filter.addCategory(Intent.CATEGORY_DEFAULT);
   ComponentName[] currentHomeActivities = getActivitiesListByActionAndCategory(context, Intent.ACTION_MAIN, Intent.CATEGORY_HOME);
   ComponentName newPreferredActivity = new ComponentName("com.android.launcher", "com.android.launcher2.Launcher");
   context.getPackageManager().addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, currentHomeActivities, newPreferredActivity);

/*   Intent intent = new Intent(Intent.ACTION_MAIN);
   intent.setClassName("com.android.launcher", "com.android.launcher2.Launcher");
   intent.addCategory(Intent.CATEGORY_LAUNCHER);
   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
   startActivity(intent); */
   finish();
}

/*private void setDefaultLauncher(Context context) {
Log.d(TAG, "SAGAR setDefaultLauncher()");
context.getPackageManager().clearPackagePreferredActivities("com.teslacoilsw.launcher");
IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
   filter.addCategory(Intent.CATEGORY_HOME);
   filter.addCategory(Intent.CATEGORY_DEFAULT);
   ComponentName[] currentHomeActivities = getActivitiesListByActionAndCategory(context, Intent.ACTION_MAIN, Intent.CATEGORY_HOME);
   ComponentName newPreferredActivity = new ComponentName("com.android.launcher", "com.android.launcher2.Launcher");
   context.getPackageManager().addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, currentHomeActivities, newPreferredActivity);

 Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.launcher");
 launchIntent.setClassName("com.android.launcher", "com.android.launcher2.Launcher"); 
 launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(launchIntent);
finish();
} */


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
                   setDefaultLauncher(context);

                } else if (selectedId == R.id.thirdPartyLauncher ) {
                   setThirdPartyLauncher(context);
                }

            }

        });

    }
}
