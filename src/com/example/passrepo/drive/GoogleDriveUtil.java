package com.example.passrepo.drive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.example.passrepo.Consts;
import com.example.passrepo.auth.GoogleAuthActivity;
import com.example.passrepo.auth.PassRepoGoogleAuthorizationCodeFlow;
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
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public class GoogleDriveUtil {

    final private static String PASS_REPO_FILE_ID = "PassRepoFileID";

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
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpTransport ht = new NetHttpTransport();
        JacksonFactory jsonF = new JacksonFactory();
        drive = new Drive.Builder(ht, jsonF, cred).build();
    }

    public void create(final java.io.File file) {
        System.out.println("Uploading file...");

        Runnable r = new Runnable() {
            public void run() {

                File driveMetaData = new File();
                driveMetaData.setTitle(Consts.PASS_REPO_REMOTE_DATABASE_FILENAME);
                driveMetaData.setDescription("Pass Repo Storage");
                driveMetaData.setMimeType("application/json");

                FileContent content = new FileContent("application/json", file);

                File r = null;
                try {
                    r = drive.files().insert(driveMetaData, content).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("id: " + r.getId());

                try {
                    drive.files().get(r.getId()).execute();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                System.out.println("Success!");
            }
        };

        runThread(r);
    }

    public void update(final java.io.File file, final String fileID) {
        System.out.println("Uploading file...");

        Runnable r = new Runnable() {
            public void run() {

                File driveMetaData = new File();
                driveMetaData.setTitle(Consts.PASS_REPO_REMOTE_DATABASE_FILENAME);
                driveMetaData.setDescription("Pass Repo Storage");
                driveMetaData.setMimeType("application/json");

                FileContent content = new FileContent("application/json", file);

                try {
                    drive.files().update(fileID, driveMetaData, content).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("Success!");
            }
        };

        runThread(r);
    }

    // Returns the saved FileID if it exists.
    public String getPassRepoFileID() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PASS_REPO_FILE_ID, null);
    }

    // Lists the files of the user in search of the PassRepo storage file, and saves its ID on the device.
    public void findAndSavePassRepoFileID(final GoogleDriveResultCallback callback) {
        Logger.i("GoogleDriveUtil", "Searching for file %s...", Consts.PASS_REPO_REMOTE_DATABASE_FILENAME);

        new Thread(new Runnable() {
            public void run() {
                Files.List request;
                try {
                    request = drive.files().list();
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError();
                    return;
                }

                do {
                    FileList files;
                    try {
                        files = request.execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError();
                        return;
                    }

                    for (File f : files.getItems()) {
                        Logger.i("GoogleDriveUtil", "Listing file %s..", f.getTitle());

                        if (f.getTitle().equals(Consts.PASS_REPO_REMOTE_DATABASE_FILENAME)) {
                            String fileID = f.getId();
                            Logger.i("GoogleDriveUtil", "Found file %s as ID: %s", Consts.PASS_REPO_REMOTE_DATABASE_FILENAME, fileID);
                            savePassRepoFileID(fileID);
                            callback.onSuccess();
                            return;
                        }
                    }

                    request.setPageToken(files.getNextPageToken());
                } while (request.getPageToken() != null && request.getPageToken().length() > 0);

                // Didn't find the file (successfully).
                callback.onSuccess();
            }
        }).start();
    }

    private void savePassRepoFileID(String fileID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = prefs.edit();
        editor.putString(PASS_REPO_FILE_ID, fileID);
        editor.commit();
    }
    
    public void downloadPassRepoFile(final String passRepoFileID, final GoogleDriveResultCallback callback) {
        Logger.i("GoogleDriveUtil", "Starting download of PassRepo file ID %s..", passRepoFileID);

        new Thread(new Runnable() {
            public void run() {
                try {
                    // TODO: Handle case in which file ID doesn't exist anymore.
                    File f = drive.files().get(passRepoFileID).execute();
                    
                    Logger.i("GoogleDriveUtil", "Sending GET request..");

                    final InputStream is = drive.getRequestFactory().buildGetRequest(new GenericUrl(f.getDownloadUrl()))
                            .execute().getContent();
                    
                    Logger.i("GoogleDriveUtil", "Downloading result..");

                    // Read file from remote inputstream..
                    String result = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                        public InputStreamReader getInput() throws IOException {
                            return new InputStreamReader(is);
                        }
                    });
                    
                    Logger.i("GoogleDriveUtil", "Writing result to disk..");
                    
                    // Write file to local disk..
                    CharStreams.write(result, new OutputSupplier<OutputStreamWriter>() {
                        public OutputStreamWriter getOutput() throws IOException {
                            return new OutputStreamWriter(context.openFileOutput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME, Context.MODE_PRIVATE), Charsets.UTF_8);
                        }
                    });
                    
                    Logger.i("GoogleDriveUtil", "Done downloading file to disk!");
                    callback.onSuccess();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError();
                }
            }
        }).start();
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

    public void authorizeIfNecessary() {
        if (!isAuthorized()) {
            authorize();
        }
    }

    public void authorize() {
        Logger.i("gdrive", "Access Token isn't saved yet, starting Google Authentication process..");
        context.startActivity(new Intent(context.getApplicationContext(), GoogleAuthActivity.class));
    }

    public boolean isAuthorized() {
        try {
            Credential cred = PassRepoGoogleAuthorizationCodeFlow.getInstance(context.getApplicationContext()).loadCredential("");

            if (cred == null || cred.getAccessToken() == null) {
                Logger.i("gdrive", "Credentials don't exist");
                return false;
            }

            // Credentials are expired.
            if (cred.getExpirationTimeMilliseconds() < new Date().getTime()) {
                Logger.i("gdrive", "Credentials have expired, considered unauthorized");
                return false;
            }

            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
