package com.example.passrepo.gdrive;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;

import com.example.passrepo.GoogleAuthenticationActivity;
import com.example.passrepo.PassRepoGoogleAuthorizationCodeFlow;
import com.example.passrepo.util.Logger;
import com.google.api.client.auth.oauth2.Credential;

public class GoogleDriveUtil {
    public static void upload(File file) {
        // TODO
    }
    
    public static void download(File file) {
        // TODO
    }
    
    public static void authorize(Context context) {
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
