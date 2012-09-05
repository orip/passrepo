package com.example.passrepo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;

import com.example.passrepo.drive.Constants;
import com.example.passrepo.util.Logger;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;

public class GoogleAuthActivity extends Activity implements GoogleAuthWebViewClientCallback {

    boolean m_isFinishedRedirect = false;
    SharedPreferences prefs;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onResume() {
        super.onResume();
        
        setTitle("Login to continue!");
        
        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);

        final GoogleAuthorizationCodeFlow flow = PassRepoGoogleAuthorizationCodeFlow.getInstance(this);
        GoogleAuthorizationCodeRequestUrl urlBuilder = flow.newAuthorizationUrl().setRedirectUri(Constants.REDIRECT_URI);
        webview.setWebViewClient(new GoogleAuthWebViewClient(flow, this));        
        webview.loadUrl(urlBuilder.build());
    }

    @Override
    public void onSuccess() {
        Logger.i("GoogleAuthActivity", "onSuccess!");
        finish();
    }

    @Override
    public void onError() {
        Logger.w("GoogleAuthActivity", "onError!");
    }
    
}
