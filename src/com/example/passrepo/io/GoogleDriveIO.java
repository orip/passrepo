package com.example.passrepo.io;

public interface GoogleDriveIO {
    void startSyncFromGoogleDriveToDisk(Runnable doneCallback);

    void saveModelAndStartSyncFromDiskToGoogleDrive(Runnable doneCallback);
}
