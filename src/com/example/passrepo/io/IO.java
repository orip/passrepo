package com.example.passrepo.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

import com.example.passrepo.Consts;
import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.drive.GoogleDriveResultCallback;
import com.example.passrepo.drive.GoogleDriveUtil;
import com.example.passrepo.drive.RealGoogleDriveUtil;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public class IO {
    
    private final static boolean DEBUG_DO_NOT_ENCRYPT = false;
    
    public static String modelToEncryptedString(Model model) {
        if (DEBUG_DO_NOT_ENCRYPT) {
            return GsonHelper.customGson.toJson(model);
        }
        
        byte[] plainText = GsonHelper.customGson.toJson(model).getBytes(Charsets.UTF_8);
        CipherText cipherText = Encryption.encrypt(plainText, model.key);
        EncryptedFile encryptedFile = new EncryptedFile(model.scryptParameters, cipherText);
        return GsonHelper.customGson.toJson(encryptedFile);
    }

    public static Model modelFromEncryptedString(String encryptedString, byte[] key) {
        if (DEBUG_DO_NOT_ENCRYPT) {
            Model result = GsonHelper.customGson.fromJson(encryptedString, Model.class);
            result.populateIdsToPasswordEntriesMap();
            return result;
        }

        EncryptedFile encryptedFile = GsonHelper.customGson.fromJson(encryptedString, EncryptedFile.class);
        String modelJson = new String(Encryption.decrypt(encryptedFile.cipherText, key), Charsets.UTF_8);
        Model result = GsonHelper.customGson.fromJson(modelJson, Model.class);
        result.populateIdsToPasswordEntriesMap();   // TODO: For some reason isn't called by the GsonHelper. Temporary workaround..
        result.key = key;
        result.scryptParameters = encryptedFile.scryptParameters;
        return result;
    }

    public static void loadModelFromDisk(final Context context) {
        try {
            Logger.i("IO", "lading model from disk..");
            String fileContents = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                public InputStreamReader getInput() throws IOException {
                    return new InputStreamReader(context.openFileInput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME), Charsets.UTF_8);
                }
            });
            
            Model.currentModel = IO.modelFromEncryptedString(fileContents, DummyContent.dummyKey);
            Logger.i("IO", "sucessfully loaded model from disk. Results are: " + fileContents);
            
        } catch (FileNotFoundException e) {
            // Model doesn't exist on disk (probably first time install), use the dummy instead.
            Model.currentModel = DummyContent.model;
            Logger.i("IO", "sucessfully loaded model from dummy content (first time install)");
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startSyncFromGoogleDriveToDisk(final Context context, final Runnable doneCallback) {
        Logger.i("IO", "startSyncFromGoogleDriveToDisk");

        final GoogleDriveUtil gdu = new RealGoogleDriveUtil(context.getApplicationContext());
        if (!gdu.isAuthorized()) {
            Logger.w("IO", "Google Drive isn't authorized yet for some reason, aborting sync");
            doneCallback.run();
        }

        // Get the saved file ID or find it remotely.
        String passRepoFileID = gdu.getPassRepoFileID();

        if (passRepoFileID != null) {
            startDownloadFromGoogleDrive(gdu, passRepoFileID, doneCallback);

        } else {
            gdu.findAndSavePassRepoFileID(new GoogleDriveResultCallback() {
                @Override
                public void onSuccess() {
                    String passRepoFileID = gdu.getPassRepoFileID();
                    if (passRepoFileID != null) {
                        Logger.i("IO", "Got the Remote File ID, Starting the download..");
                        startDownloadFromGoogleDrive(gdu, passRepoFileID, doneCallback);
                    } else {
                        Logger.i("IO", "Remote file doesn't exist, aborting download.");
                        doneCallback.run();
                    }
                }

                @Override
                public void onError() {
                    Logger.w("IO", "Failed fetching the remote file ID from Google Drive (connectivity errors? Authentication Problem?)..");
                    doneCallback.run();
                }
            });
        }
    }

    private static void startDownloadFromGoogleDrive(final GoogleDriveUtil gdu, final String fileID, final Runnable doneCallback) {
        gdu.downloadPassRepoFile(fileID, new GoogleDriveResultCallback() {
            @Override
            public void onSuccess() {
                Logger.i("IO", "Successfully downloaded the remote file to the disk.");
                doneCallback.run();
            }

            @Override
            public void onError() {
                Logger.w("IO", "Failed downloaded the remote file to the disk..");
                doneCallback.run();
            }
        });

    }

    public static void saveModelAndStartSyncFromDiskToGoogleDrive(final Context context, final Runnable doneCallback) {
        // Save the encrypted result to the local disk.
        File f;
        try {
            CharStreams.write(IO.modelToEncryptedString(Model.currentModel), new OutputSupplier<OutputStreamWriter>() {
                public OutputStreamWriter getOutput() throws IOException {
                    return new OutputStreamWriter(context.openFileOutput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME, Context.MODE_PRIVATE));
                }
            });
            f = new File(new File("/mnt/sdcard"), Consts.PASS_REPO_LOCAL_DATABASE_FILENAME);
            Files.write(IO.modelToEncryptedString(Model.currentModel), f, Charsets.UTF_8);
            Logger.i("IO", "saved model to disk");
        } catch (IOException e) {
            // TODO
            Logger.w("IO", "error saving model to disk");
            throw new RuntimeException(e);
        }
        
        // Start the asynchronous upload of the local file to Google Drive.
        final GoogleDriveUtil gdu = new RealGoogleDriveUtil(context.getApplicationContext());
        if (!gdu.isAuthorized()) {
            Logger.w("IO", "Google Drive isn't authorized yet for some reason, aborting upload sync");
            doneCallback.run();
            return;
        }
        
        new RealGoogleDriveUtil(context.getApplicationContext()).uploadPassRepoFileToGoogleDrive(
                f, new GoogleDriveResultCallback() {
            public void onSuccess() {
                Logger.i("IO", "Successfully uploaded the local file to Google Drive.");
                doneCallback.run();
            }
            
            public void onError() {
                Logger.w("IO", "Failed uploading the local file to Google Drive...");
                doneCallback.run();
            }
        });
    }

}
