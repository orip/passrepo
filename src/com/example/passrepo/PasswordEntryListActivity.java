package com.example.passrepo;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.gdrive.GoogleDriveUtil;
import com.example.passrepo.io.IO;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public class PasswordEntryListActivity extends FragmentActivity implements PasswordEntryListFragment.Callbacks {
    private static final String PASSWORD_DATABASE_FILENAME = "password_database.json";
    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordentry_list);

        if (findViewById(R.id.passwordentry_detail_container) != null) {
            mTwoPane = true;
            ((PasswordEntryListFragment) getSupportFragmentManager().findFragmentById(R.id.passwordentry_list))
                    .setActivateOnItemClick(true);
        }
        
        loadModel();
    }

    private void loadModel() {
        if (Model.currentModel == null) {
            Logger.i("IO", "loading model");
            /*
            try {                
                String fileContents = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                    public InputStreamReader getInput() throws IOException {
                        return new InputStreamReader(openFileInput(PASSWORD_DATABASE_FILENAME), Charsets.UTF_8);
                    }
                });
                */
                String fileContents = new GoogleDriveUtil(this).download();
                Model.currentModel = IO.modelFromEncryptedString(fileContents, DummyContent.dummyKey);
                Logger.i("IO", "sucessfully loaded model from disk");
                /*
            } catch (IOException e) {
                Model.currentModel = DummyContent.model;
                Logger.i("IO", "loaded dummy model");
            }*/
        }
    }
    
    private void saveModel() {
        try {
            CharStreams.write(IO.modelToEncryptedString(Model.currentModel), new OutputSupplier<OutputStreamWriter>() {
                public OutputStreamWriter getOutput() throws IOException {
                    return new OutputStreamWriter(openFileOutput(PASSWORD_DATABASE_FILENAME, MODE_PRIVATE));
                }
            });
            File f = new File(new File("/mnt/sdcard"), PASSWORD_DATABASE_FILENAME);
            Files.write(IO.modelToEncryptedString(Model.currentModel), f, Charsets.UTF_8);
            Logger.i("IO", "saved model to disk");
            new GoogleDriveUtil(this).upload(f);            
            
            Logger.i("IO", "saved model to drive!!!");
        } catch (IOException e) {
            Logger.i("IO", "error saving model to disk");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(PasswordEntryDetailFragment.ARG_ITEM_ID, id);
            PasswordEntryDetailFragment fragment = new PasswordEntryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.passwordentry_detail_container, fragment).commit();

        } else {
            Intent detailIntent = new Intent(this, PasswordEntryDetailActivity.class);
            detailIntent.putExtra(PasswordEntryDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        testDriveEncryption();
        new GoogleDriveUtil(this).authorize();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveModel();
    }

    @SuppressWarnings("unused")
    private void testDriveEncryption() {
        String encryptedString = IO.modelToEncryptedString(DummyContent.model);
        Model decryptedModel = IO.modelFromEncryptedString(encryptedString, DummyContent.model.key);
        Logger.i("TEST", "originalModel=%s", GsonHelper.customGson.toJson(DummyContent.model));
        Logger.i("TEST", "encryptedString=%s", encryptedString);
        Logger.i("TEST", "decryptedModel=%s", GsonHelper.customGson.toJson(decryptedModel));

        if (false) {
            ScryptParameters scryptParameters = new ScryptParameters();
            byte[] key = PasswordHasher.hash("foo", scryptParameters);
            CipherText encrypted = Encryption.encrypt("What's up man?".getBytes(Charsets.UTF_8), key);
            String decrypted = new String(Encryption.decrypt(encrypted, key), Charsets.UTF_8);
            Logger.i("TEST", "derivedKey=%s", Base64.encodeToString(key, Base64.NO_WRAP));
            Logger.i("TEST", "encrypted=%s", Base64.encodeToString(encrypted.bytes, Base64.NO_WRAP));
            Logger.i("TEST", "decrypted=%s", decrypted);
        }
    }
}
