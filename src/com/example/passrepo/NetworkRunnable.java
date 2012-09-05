package com.example.passrepo;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class NetworkRunnable implements Runnable {

    GoogleAuthorizationCodeTokenRequest m_req;
    
    GoogleTokenResponse m_res;
    public NetworkRunnable(GoogleAuthorizationCodeTokenRequest req) {
        this.m_req = req;
    }

    @Override
    public void run() {
        try {
            m_res = this.m_req.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public GoogleTokenResponse getRes() {
        return m_res;
    }
}
