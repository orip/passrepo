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

    final private static String PASS_REPO_FILE_ID_KEY = "PassRepoFileID";

    private final Context context;
    private final Drive drive;

    public GoogleDriveUtil(Context context) {
        this.context = context;

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

    // Returns the saved FileID if it exists.
    public String getPassRepoFileID() {
        return getPrefs().getString(PASS_REPO_FILE_ID_KEY, null);
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void savePassRepoFileID(String fileID) {
        getPrefs().edit().putString(PASS_REPO_FILE_ID_KEY, fileID).commit();
    }

    private void clearPassRepoFileID() {
        getPrefs().edit().remove(PASS_REPO_FILE_ID_KEY).commit();
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
                            Logger.i("GoogleDriveUtil", "Found file %s as ID: %s", Consts.PASS_REPO_REMOTE_DATABASE_FILENAME,
                                    fileID);
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

    // Asynchronously downloads the given fileID to the disk (hard-coded path), and calls the given callback in the end.
    public void downloadPassRepoFile(final String passRepoFileID, final GoogleDriveResultCallback callback) {
        Logger.i("GoogleDriveUtil", "Starting download of PassRepo file ID %s..", passRepoFileID);

        new Thread(new Runnable() {
            public void run() {
                File f;
                try {
                    f = drive.files().get(passRepoFileID).execute();
                } catch (IOException e) {
                    Logger.i("GoogleDriveUtil", "PassRepo file ID was not found, probably deleted or moved. Clearing ID..");
                    clearPassRepoFileID();
                    callback.onError();
                    return;
                }

                try {
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

                    Logger.i("GoogleDriveUtil", "Writing result to disk: " + result);

                    // Write file to local disk..
                    CharStreams.write(result, new OutputSupplier<OutputStreamWriter>() {
                        public OutputStreamWriter getOutput() throws IOException {
                            return new OutputStreamWriter(context.openFileOutput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME,
                                    Context.MODE_PRIVATE), Charsets.UTF_8);
                        }
                    });

                    Logger.i("GoogleDriveUtil", "Done downloading file to disk!");
                    callback.onSuccess();

                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.i("GoogleDriveUtil", "Failed downloading file, clearing its ID just in case..");
                    clearPassRepoFileID();
                    callback.onError();
                }
            }
        }).start();
    }

    public void uploadPassRepoFileToGoogleDrive(final java.io.File localPassRepoFile, final GoogleDriveResultCallback callback) {
        Logger.i("GoogleDriveUtil", "Starting upload of PassRepo file to Google Drive..");

        new Thread(new Runnable() {
            public void run() {
                File driveMetaData = new File();
                driveMetaData.setTitle(Consts.PASS_REPO_REMOTE_DATABASE_FILENAME);
                driveMetaData.setDescription("Pass Repo Storage");
                driveMetaData.setMimeType("application/json");

                FileContent content = new FileContent("application/json", localPassRepoFile);

                String existingPassRepoFileID = getPassRepoFileID();

                File r = null;
                if (existingPassRepoFileID == null) {
                    Logger.i("GoogleDriveUtil", "Creating a new file (PassRepo file ID doesn't exist)..");
                    
                    try {
                        r = drive.files().insert(driveMetaData, content).execute();
                    } catch (IOException e) {
                        Logger.w("GoogleDriveUtil", "Failed uploading the file (connectivity/authentication issues?)");
                        e.printStackTrace();
                        callback.onError();
                        return;
                    }

                    String newPassRepoFileID = r.getId();
                    savePassRepoFileID(newPassRepoFileID);
                    
                    Logger.i("GoogleDriveUtil", "Successfully created a new file in Google Drive and saved its ID: %s",
                            newPassRepoFileID);

                } else {
                    Logger.i("GoogleDriveUtil", "Updating the remote file ID: %s", existingPassRepoFileID);
                    
                    try {
                        r = drive.files().update(existingPassRepoFileID, driveMetaData, content).execute();
                    } catch (IOException e) {
                        Logger.i("GoogleDriveUtil", "Failed updating remote file %s, its ID may have been removed, clearing it..", existingPassRepoFileID);
                        e.printStackTrace();
                        clearPassRepoFileID();
                        callback.onError();
                        return;
                    }
                                        
                    Logger.i("GoogleDriveUtil", "Successfully updated the existing file in Google Drive: %s", existingPassRepoFileID);
                }
                
                callback.onSuccess();
            }
        }).start();
    }
    
    // Starts the Google OAuth authorization process.
    public void authorize() {
        context.startActivity(new Intent(context.getApplicationContext(), GoogleAuthActivity.class));
    }

    // Checks if the Google OAuth credentials exist and are valid.
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
            // TODO
            throw new RuntimeException(e);
        }
    }
    
    // Clears the authorization credentials.
    public void clearAuthorizationCache() {
        try {
            new SharedPreferencesCredentialStore(context).delete(null, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
