package com.example.passrepo;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.widget.TextView;

import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.Credential;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ((TextView)findViewById(R.id.foo)).setText(Base64.encodeToString(PasswordHasher.hash("foo"), Base64.NO_WRAP));
        /*
        try {
            new SharedPreferencesCredentialStore(this).delete(null, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
        
    @Override
    public void onResume() {
        super.onResume();

        Credential cred = null;
        try {
            cred = PassRepoGoogleAuthorizationCodeFlow.getInstance(this).loadCredential("");
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        if (cred == null) {
            System.out.println("Access Token isn't saved yet, starting Google Authentication process..");
            startActivity(new Intent().setClass(getApplicationContext(),GoogleAuthenticationActivity.class));
        } else {
            setTitle("Logged-in! Start using Drive!");
            System.out.println("Logged-in accessToken=" + cred.getAccessToken());
            startActivity(new Intent().setClass(getApplicationContext(),GoogleDriveActivity.class));
        }
    }
}
