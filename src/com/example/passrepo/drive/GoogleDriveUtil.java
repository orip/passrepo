package com.example.passrepo.drive;

public interface GoogleDriveUtil {
    // Returns the saved FileID if it exists.
    String getPassRepoFileID();

    // Lists the files of the user in search of the PassRepo storage file, and saves its ID on the device.
    void findAndSavePassRepoFileID(GoogleDriveResultCallback callback);

    // Asynchronously downloads the given fileID to the disk (hard-coded path), and calls the given callback in the end.
    void downloadPassRepoFile(String passRepoFileID, GoogleDriveResultCallback callback);

    void uploadPassRepoFileToGoogleDrive(java.io.File localPassRepoFile, GoogleDriveResultCallback callback);

    // Starts the Google OAuth authorization process.
    void authorize();

    // Checks if the Google OAuth credentials exist and are valid.
    boolean isAuthorized();

    // Clears the authorization credentials.
    void clearAuthorizationCache();
}
