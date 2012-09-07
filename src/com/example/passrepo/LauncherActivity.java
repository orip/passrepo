package com.example.passrepo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.passrepo.drive.GoogleDriveUtil;
import com.example.passrepo.util.Logger;

public class LauncherActivity extends Activity {
    
    private GoogleDriveUtil googleDriveUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        googleDriveUtil = new GoogleDriveUtil(this);
        
        //setContentView(R.layout.activity_passwordentry_list);
    }

    protected void onResume() {
        super.onResume();
        
        // Start authentication activity if required. Will return here when done.
        if (!googleDriveUtil.isAuthorized()) {
            // TODO: Login screen instead of automatically redirecting to the next activity.
            Logger.i("LauncherActivity", "Not authorized, directing to login activity");
            googleDriveUtil.authorize();
            
        } else {
            Logger.i("LauncherActivity", "Authorized, directing to passwords list activity");
            startActivity(new Intent(this, PasswordEntryListActivity.class));
        }
    }
}
