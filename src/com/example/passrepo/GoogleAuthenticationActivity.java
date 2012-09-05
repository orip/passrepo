package com.example.passrepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.drive.util.QueryStringParser;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

public class GoogleAuthenticationActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ((TextView)findViewById(R.id.foo)).setText(Base64.encodeToString(PasswordHasher.hash("foo"), Base64.NO_WRAP));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    static final String CLIENT_ID = "962290543322.apps.googleusercontent.com";
    static final String CLIENT_SECRET = "McwLu2ChbXhVZd02c4C3SZg5";
    static final List<String> SCOPES = new ArrayList<String>(
            Arrays.asList("https://www.googleapis.com/auth/drive.file"));
    static final String REDIRECT_URI = "http://localhost/oauth2callback";

    
    @Override
    public void onResume() {
        super.onResume();

        tryAuth();
    }
    
    
    GoogleAuthorizationCodeFlow flow;
    boolean m_isFinished = false;
    public synchronized void tryAuth() {
        HttpTransport ht = new NetHttpTransport();
        JacksonFactory jsonF = new JacksonFactory();
        
        
        flow = new GoogleAuthorizationCodeFlow.Builder(
                ht, jsonF, CLIENT_ID, CLIENT_SECRET, SCOPES).build();
        
        // TODO already logged in?
        GoogleAuthorizationCodeRequestUrl urlBuilder = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI);
       
        System.out.println(urlBuilder.build());
        
        WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);
        
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setTitle("Login to continue!");
            }            
            
            public void onPageFinished(WebView view, String url) {
                m_isFinished = true;
                System.out.println("page finished: " + url);
                
                if (url.startsWith("http://local") && !m_isFinished) {
                    view.setVisibility(View.INVISIBLE);
                    setTitle("Login done!");
                    
                    String authorizationCode = extractParamFromUrl(url, "code");
                    
                    NetworkRunnable nr = new NetworkRunnable(
                            flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI));
                    
                    Thread t = new Thread(nr);
                    t.start();
                    System.out.println("Waiting on thread..");
                    try {
                        t.join(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("done waiting!");
                    
                    GoogleTokenResponse gtk = nr.getRes();
                    gtk.getAccessToken();
                    gtk.getRefreshToken();
                    
                }
            }
            private String extractParamFromUrl(String url,String paramName) {
                String queryString = url.substring(url.indexOf("?", 0)+1,url.length());
                QueryStringParser queryStringParser = new QueryStringParser(queryString);
                return queryStringParser.getQueryParamValue(paramName);
            } 

        });
        
        String url = "http://www.ynet.co.il";
        url = urlBuilder.build();
        webview.loadUrl(url);
    }
}
