package com.example.passrepo.gdrive;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.example.passrepo.GoogleAuthenticationActivity;
import com.example.passrepo.PassRepoGoogleAuthorizationCodeFlow;
import com.example.passrepo.util.Logger;
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

public class GoogleDriveUtil {
    
    Context context = null;
    Drive drive = null;
    
    public GoogleDriveUtil(Context context) {
        Logger.i("gdriveutil", "Starting to init");
        this.context = context;
        setDriveInstance();
        Logger.i("gdriveutil", "init done");
    }
    
    private void setDriveInstance() {
        GoogleAuthorizationCodeFlow flow = PassRepoGoogleAuthorizationCodeFlow.getInstance(context);
        
        Credential cred = null;
        try {
            cred = flow.loadCredential("");
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        HttpTransport ht = new NetHttpTransport();
        JacksonFactory jsonF = new JacksonFactory();
        drive = new Drive.Builder(ht, jsonF, cred).build();        
    }
    
    
    
    public void upload(final java.io.File file) {        
        System.out.println("Uploading file...");
        
        Runnable r = new Runnable() {
            public void run() {

                File driveMetaData = new File();
                driveMetaData.setTitle("PassRepoStorage");
                driveMetaData.setDescription("Pass Repo Storage");
                driveMetaData.setMimeType("application/json");
                
                FileContent content = new FileContent("application/json", file);
                
                try {
                    // TODO if file exists, update 
                    drive.files().insert(driveMetaData, content).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                System.out.println("Success!");
            }
        };
        
        runThread(r);
    }
    
    public void download(File file) {
        // TODO
        Runnable r = new Runnable() {
            public void run() {
                
            }
        };
        
        runThread(r);
    }
    
    private void runThread(Runnable r) {
        Thread t = new Thread(r);
        t.start();
        System.out.println("Waiting on thread..");
        try {
            t.join(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");        
    }    
    
    public void authorize() {
        Credential cred = null;
        try {
            cred = PassRepoGoogleAuthorizationCodeFlow.getInstance(context.getApplicationContext()).loadCredential("");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        
        if (cred == null) {
            Logger.i("gdrive", "Access Token isn't saved yet, starting Google Authentication process..");
            context.startActivity(new Intent().setClass(context.getApplicationContext(), GoogleAuthenticationActivity.class));
        }
    }
}