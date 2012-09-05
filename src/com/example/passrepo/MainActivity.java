package com.example.passrepo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        ScryptParameters scryptParameters = new ScryptParameters();
        byte[] key = PasswordHasher.hash("foo", scryptParameters);
        CipherText encrypted = Encryption.encrypt("What's up man?".getBytes(Charsets.UTF_8), key);
        String decrypted = new String(Encryption.decrypt(encrypted, key), Charsets.UTF_8);
        Logger.i(TAG, "derivedKey=%s", Base64.encodeToString(key, Base64.NO_WRAP));
        Logger.i(TAG, "encrypted=%s", Base64.encodeToString(encrypted.bytes, Base64.NO_WRAP));
        Logger.i(TAG, "decrypted=%s", decrypted);
    }
}
