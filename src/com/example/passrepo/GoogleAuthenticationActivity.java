package com.example.passrepo;

import java.io.IOException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.passrepo.drive.Constants;
import com.example.passrepo.util.QueryStringParser;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class GoogleAuthenticationActivity extends Activity {

    boolean m_isFinishedRedirect = false;
    SharedPreferences prefs;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        tryAuth();
    }
        
    public synchronized void tryAuth() {
        final GoogleAuthorizationCodeFlow flow = PassRepoGoogleAuthorizationCodeFlow.getInstance(this);
        
        // TODO already logged in?
        GoogleAuthorizationCodeRequestUrl urlBuilder = flow.newAuthorizationUrl().setRedirectUri(Constants.REDIRECT_URI);
        
        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        
        webview.setWebViewClient(new WebViewClient() {
            
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("page started: " + url);
                
                super.onPageStarted(view, url, favicon);
                setTitle("Login to continue!");
            }            
            
            public void onPageFinished(WebView view, String url) {
                System.out.println("page finished: " + url);
                
                if (!url.startsWith("http://local")) {
                    super.onPageFinished(view, url);
                    return;
                }
                
                view.setVisibility(View.INVISIBLE);
                setTitle("Login done!");
                
                // Avoid multiple parallel callbacks (happen for some reason?)
                synchronized(this) {
                    if (m_isFinishedRedirect) {
                        return;
                    }
                    
                    m_isFinishedRedirect = true;
                }
            
                String authorizationCode = extractParamFromUrl(url, "code");
                
                GoogleAuthorizationCodeTokenRequest tokenRequest =
                        flow.newTokenRequest(authorizationCode).setRedirectUri(Constants.REDIRECT_URI);
                
                NetworkRunnable nr = new NetworkRunnable(tokenRequest);
                
                Thread t = new Thread(nr);
                t.start();
                System.out.println("Waiting on thread..");
                try {
                    t.join(20000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("done waiting!");
                
                GoogleTokenResponse gtk = nr.getRes();
                
                Credential credential = null;
                try {
                    credential = flow.createAndStoreCredential(gtk, "");
                    
                    System.out.println("Finished storing credentials! Going back!");
                    
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        webview.loadUrl(urlBuilder.build());
    }
    
    private String extractParamFromUrl(String url,String paramName) {
        String queryString = url.substring(url.indexOf("?", 0)+1,url.length());
        QueryStringParser queryStringParser = new QueryStringParser(queryString);
        return queryStringParser.getQueryParamValue(paramName);
    }
}
