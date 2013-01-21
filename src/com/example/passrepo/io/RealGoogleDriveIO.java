package com.example.passrepo.io;

import android.content.Context;
import com.example.passrepo.Consts;
import com.example.passrepo.drive.GoogleDriveResultCallback;
import com.example.passrepo.drive.GoogleDriveUtil;
import com.example.passrepo.drive.StubGoogleDriveUtil;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class RealGoogleDriveIO implements GoogleDriveIO {
    private final Context context;

    public RealGoogleDriveIO(final Context context) {
        this.context = context;
    }

    @Override
    public void startSyncFromGoogleDriveToDisk(final Runnable doneCallback) {
        Logger.i("IO", "startSyncFromGoogleDriveToDisk");

        final GoogleDriveUtil gdu = getGoogleDriveUtil();
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

    private GoogleDriveUtil getGoogleDriveUtil() {
        return new StubGoogleDriveUtil(context.getApplicationContext());
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

    @Override
    public void saveModelAndStartSyncFromDiskToGoogleDrive(final Runnable doneCallback) {
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
        if (!getGoogleDriveUtil().isAuthorized()) {
            Logger.w("IO", "Google Drive isn't authorized yet for some reason, aborting upload sync");
            doneCallback.run();
            return;
        }

        getGoogleDriveUtil().uploadPassRepoFileToGoogleDrive(
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
