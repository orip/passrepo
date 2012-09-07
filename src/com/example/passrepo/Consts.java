package com.example.passrepo;

import java.util.Arrays;
import java.util.List;

public class Consts {
    public static final String appPrefix = "com.example.passrepo";
    public static final String VIEW_ACTION = appPrefix + ".action.view.password_entry";
    public static final String EDIT_ACTION = appPrefix + ".action.edit.password_entry";
    public static final String ARG_ITEM_ID = "item_id";
    public static final int COPY_PASSWORD_NOTIFICATION_ID = 1;
    
    //static public final String CLIENT_ID = "962290543322.apps.googleusercontent.com";    
    //static public final String CLIENT_SECRET = "McwLu2ChbXhVZd02c4C3SZg5";
    
    // Google API Console => API Access => Client ID for web applications => Client ID
    static public final String GOOGLE_AUTH_CLIENT_ID = "391091595646.apps.googleusercontent.com";
    
    // Google API Console => API Access => Client ID for web applications => Client Secret
    static public final String GOOGLE_AUTH_CLIENT_SECRET = "WgSHIAVsPHpWUedHNaGt50WE";
    
    static public final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/drive");
    // "https://www.googleapis.com/auth/drive.file", "https://www.googleapis.com/auth/drive.readonly.metadata",
    
    // Google API Console => API Access => Client ID for web applications => Redirect URIs
    static public final String REDIRECT_URI = "http://localhost/oauth2callback";
}
