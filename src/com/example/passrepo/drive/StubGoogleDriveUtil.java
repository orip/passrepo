package com.example.passrepo.drive;

import android.content.Context;

import java.io.File;

/**
 * Helper class to work without GDrive
 */
public class StubGoogleDriveUtil implements GoogleDriveUtil {
    public StubGoogleDriveUtil(Context applicationContext) {
    }

    @Override
    public String getPassRepoFileID() {
        return null;
    }

    @Override
    public void findAndSavePassRepoFileID(GoogleDriveResultCallback callback) {
    }

    @Override
    public void downloadPassRepoFile(String passRepoFileID, GoogleDriveResultCallback callback) {
    }

    @Override
    public void uploadPassRepoFileToGoogleDrive(File localPassRepoFile, GoogleDriveResultCallback callback) {
    }

    @Override
    public void authorize() {
    }

    @Override
    public boolean isAuthorized() {
        return true;
    }

    @Override
    public void clearAuthorizationCache() {
    }
}
