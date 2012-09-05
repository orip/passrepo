package com.example.passrepo;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.widget.TextView;

import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.drive.Constants;
import com.example.passrepo.store.CredentialStore;
import com.example.passrepo.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GoogleDriveActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
        
    @Override
    public void onResume() {
        super.onResume();
        
        HttpTransport ht = new NetHttpTransport();
        JacksonFactory jsonF = new JacksonFactory();        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                ht, jsonF, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.SCOPES).build();
        
        Credential cred = null;
        try {
            cred = flow.loadCredential(null);
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        Drive service = new Drive.Builder(ht, jsonF, cred);
        
        ((TextView)findViewById(R.id.foo)).setText(Base64.encodeToString(PasswordHasher.hash("foo"), Base64.NO_WRAP));
    }
}
