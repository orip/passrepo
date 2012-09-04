package com.example.passrepo;

import com.example.passrepo.crypto.PasswordHasher;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.widget.TextView;

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
}
