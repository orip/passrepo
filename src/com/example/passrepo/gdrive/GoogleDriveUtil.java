package com.example.passrepo.gdrive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

import com.example.passrepo.GoogleAuthenticationActivity;
import com.example.passrepo.PassRepoGoogleAuthorizationCodeFlow;
import com.example.passrepo.store.SharedPreferencesCredentialStore;
import com.example.passrepo.util.Logger;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

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
    
    final String FILENAME = "PassRepoStorage";
    
    public void upload(final java.io.File file) {        
        System.out.println("Uploading file...");
        
        Runnable r = new Runnable() {
            public void run() {

                File driveMetaData = new File();
                driveMetaData.setTitle(FILENAME);
                driveMetaData.setDescription("Pass Repo Storage");
                driveMetaData.setMimeType("application/json");
                
                FileContent content = new FileContent("application/json", file);
                
                //File result = null;
                try {
                    // TODO if file exists, update 
                    drive.files().insert(driveMetaData, content).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                
                System.out.println("Success!");
            }
        };
        
        runThread(r);
    }
    
    private String result;
    public String download() {
        System.out.println("Downloading file...");
        authorize();
    
        Runnable r = new Runnable() {
            public void run() {                
                try {
                    String fileId = null;
                    Files.List request = drive.files().list();

                    boolean success = false;
                    do {
                      try {
                        FileList files = request.execute();

                        for (File f : files.getItems()) {
                            if (f.getTitle() == FILENAME) {
                                System.out.println("FOUND!!!!!");
                                fileId = f.getId();
                                success = true;
                                break;
                            }                            
                        }
                        
                        request.setPageToken(files.getNextPageToken());
                      } catch (IOException e) {
                        System.out.println("An error occurred: " + e);
                        request.setPageToken(null);
                      }
                    } while (request.getPageToken() != null && request.getPageToken().length() > 0 && !success);

                    // Get the file!
                    File f = drive.files().get(fileId).execute();
                    final InputStream is = drive.getRequestFactory().buildGetRequest(new GenericUrl(
                            f.getDownloadUrl())).execute().getContent();
                    
                    result = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                        public InputStreamReader getInput() throws IOException {
                            return new InputStreamReader(is);
                        }
                    });

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                
                System.out.println("Success!");
            }
        };
        
        runThread(r);
        return result;
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
    
    public void clearCache() {
        try {
            new SharedPreferencesCredentialStore(context).delete(null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void authorize() {
        clearCache();
        
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
