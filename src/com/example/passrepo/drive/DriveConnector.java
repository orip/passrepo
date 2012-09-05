package com.example.passrepo.drive;


public class DriveConnector {
/*
    static final String CLIENT_ID = "962290543322.apps.googleusercontent.com";
    static final String CLIENT_SECRET = "McwLu2ChbXhVZd02c4C3SZg5";
    static final List<String> SCOPES = new ArrayList<String>(
            Arrays.asList("https://www.googleapis.com/auth/drive.file"));
    static final String REDIRECT_URI = "http://localhost/oauth2callback";
    
    public DriveConnector() {         
    }

    public void auth() {
        
    }
    
    
    
    public void redirectUserToGrantScreen() {
        HttpTransport ht = new NetHttpTransport();
        JacksonFactory jsonF = new JacksonFactory();
        
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                ht, jsonF, CLIENT_ID, CLIENT_SECRET, SCOPES).build();
        
        GoogleAuthorizationCodeRequestUrl urlBuilder = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI);
       
        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        
        webview.loadUrl(urlBuilder.build());
    }

    public void getAccessToken() {
        GoogleAuthorizationCodeFlow flow;
        
        
        String autorizationCode = req.getParameter("code");
        GoogleTokenResponse tokenResponse = 
            flow.newTokenRequest(autorizationCode).setRedirectUri(REDIRECT_URI).execute();
            
            String accessToken = tokenResponse.getAccessToken();
            String refreshToken = tokenResponse.getRefreshToken();
    }
    */
}
