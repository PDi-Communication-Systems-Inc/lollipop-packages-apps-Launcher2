package com.android.launcher2;

import java.io.*;

import android.util.Log;


//test functions
//Utils.getMACAddress("wlan0");
//Utils.getMACAddress("eth0");
//Utils.getIPAddress(true); // IPv4
//Utils.getIPAddress(false); // IPv6 

public class Utils {
	
	private static final String TAG = "Utils";
    
    /**
     * Copy directory from one location to another
     * @param File Sourcelocation
     * @param File targetlocation
     * @return  void
     * @throws java.io.IOException
     */
    public static void copyDirectoryOneLocationToAnotherLocationNoAdmin(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {        	
        		//do not copy anything under admin
        	Log.i(TAG, "Dir path is: " + sourceLocation.getAbsolutePath() );
		    	if(sourceLocation.getAbsolutePath().equalsIgnoreCase( "/mnt/shell/emulated/0/admin/"))  {
		    		Log.i(TAG, "Not copying the folder " + sourceLocation.getAbsolutePath() );
		    		return;
		    	}		    		    	
        	
            if (!targetLocation.exists()) {
                targetLocation.mkdirs();
            }
            
            Log.i(TAG, "sourceLocation " + sourceLocation.getAbsolutePath() );
            Log.i(TAG, "targetLocation " + targetLocation.getAbsolutePath() );

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {
            	copyDirectoryOneLocationToAnotherLocationNoAdmin(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[4096];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }    
   

}