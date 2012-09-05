package com.example.passrepo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.Menu;
import android.widget.TextView;

import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.store.CredentialStore;
import com.example.passrepo.store.SharedPreferencesCredentialStore;

public class MainActivity extends Activity {

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
        
    @Override
    public void onResume() {
        super.onResume();
        
        CredentialStore credentialStore = new SharedPreferencesCredentialStore(
                PreferenceManager.getDefaultSharedPreferences(this));
        String[] credentials = credentialStore.read();
        
        if (credentials[0] == null) {
            System.out.println("Access Token isn't saved yet, starting Google Authentication process..");
            startActivity(new Intent().setClass(getApplicationContext(),GoogleAuthenticationActivity.class));
        }
        
        setTitle("Logged-in! Start using Drive!");
        System.out.println("Logged-in accessToken=" + credentials[0]);
    }
}
