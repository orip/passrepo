package com.example.passrepo;


import android.content.Context;

import com.example.passrepo.drive.Constants;
import com.example.passrepo.store.SharedPreferencesCredentialStore;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class PassRepoGoogleAuthorizationCodeFlow {
    
    private static GoogleAuthorizationCodeFlow instance = null;
    
    public static synchronized GoogleAuthorizationCodeFlow getInstance(Context context) {
        if (instance == null) {
            HttpTransport ht = new NetHttpTransport();
            JacksonFactory jsonF = new JacksonFactory();        
            instance = new GoogleAuthorizationCodeFlow.Builder(
                    ht, jsonF, Constants.CLIENT_ID, Constants.CLIENT_SECRET, Constants.SCOPES)
                    .setCredentialStore(new SharedPreferencesCredentialStore(context)).build();
            
        }
        return instance;
    }
}
