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
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

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
        
        System.out.println("GoogleDriveActivity:onResume started");
        
        GoogleAuthorizationCodeFlow flow = PassRepoGoogleAuthorizationCodeFlow.getInstance();
        
        Credential cred = null;
        try {
            cred = flow.loadCredential(null);
        } catch(IOException e) {
            e.printStackTrace();
        }

        HttpTransport ht = new NetHttpTransport();
        JacksonFactory jsonF = new JacksonFactory();
        Drive service = new Drive.Builder(ht, jsonF, cred).build();
        
        File body = new File();
        body.setTitle("PassRepoStorage");
        body.setDescription("Pass Repo Storage");
        body.setMimeType("application/json");
        
        java.io.File fileContent = new java.io.File("/tmp/filename");
        FileContent mediaContent = new FileContent("application/json", fileContent);
        
        try {
            System.out.println("Uploading file...");
            File file = service.files().insert(body, mediaContent).execute();
            
            ((TextView)findViewById(R.id.foo)).setText("SUCCESS! Uploaded a file!");
            
            System.out.println("Success!");
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
