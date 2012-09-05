package com.example.passrepo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.DerivedKey;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DerivedKey derivedKey = PasswordHasher.hash("foo");
        CipherText encrypted = Encryption.encrypt("What's up man?".getBytes(Charsets.UTF_8), derivedKey.bytes);
        String decrypted = new String(Encryption.decrypt(encrypted, derivedKey.bytes), Charsets.UTF_8);
        Logger.i(TAG, "derivedKey=", Base64.encodeToString(derivedKey.bytes, Base64.NO_WRAP));
        Logger.i(TAG, "encrypted=", Base64.encodeToString(encrypted.bytes, Base64.NO_WRAP));
        Logger.i(TAG, "decrypted=", decrypted);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
