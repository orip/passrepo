package com.example.passrepo;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

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

        final Context context = this;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("Uploading file...");
                GoogleAuthorizationCodeFlow flow = PassRepoGoogleAuthorizationCodeFlow.getInstance(context);
                
                Credential cred = null;
                try {
                    cred = flow.loadCredential("");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                
                System.out.println("UploadFile accessToken=" + cred.getAccessToken());

                HttpTransport ht = new NetHttpTransport();
                JacksonFactory jsonF = new JacksonFactory();
                Drive service = new Drive.Builder(ht, jsonF, cred).build();
                
                File body = new File();
                body.setTitle("PassRepoStorage");
                body.setDescription("Pass Repo Storage");
                body.setMimeType("application/json");
                
                java.io.File fileContent = new java.io.File("/mnt/sdcard/test");
                try {
                    Files.write("FOO BAR", fileContent, Charsets.UTF_8);
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
                FileContent mediaContent = new FileContent("application/json", fileContent);

                try {
                    File file = service.files().insert(body, mediaContent).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                System.out.println("Success!");
            }
        };
        
        Thread t = new Thread(r);
        t.start();
        System.out.println("Waiting on thread..");
        try {
            t.join(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("done waiting!");
    }
}
