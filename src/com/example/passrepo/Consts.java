package com.example.passrepo;

import java.util.Arrays;
import java.util.List;

public class Consts {
    public static final String appPrefix = "com.example.passrepo";
    public static final String VIEW_ACTION = appPrefix + ".action.view.password_entry";
    public static final String EDIT_ACTION = appPrefix + ".action.edit.password_entry";
    public static final String ARG_ITEM_ID = "item_id";
    public static final int COPY_PASSWORD_NOTIFICATION_ID = 1;
    
    
    static public final String GOOGLE_AUTH_CLIENT_ID = "962290543322.apps.googleusercontent.com";    
    static public final String GOOGLE_AUTH_CLIENT_SECRET = "McwLu2ChbXhVZd02c4C3SZg5";
    
    static public final List<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/drive");
    // "https://www.googleapis.com/auth/drive.file", "https://www.googleapis.com/auth/drive.readonly.metadata",
    
    static public final String REDIRECT_URI = "http://localhost/oauth2callback";
    
    static public final String EXTRA_CREDENTIALS = "ExtraCredentials";

}
