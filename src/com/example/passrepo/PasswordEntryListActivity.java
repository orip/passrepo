package com.example.passrepo;

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
import com.google.common.base.Preconditions;

public class PasswordEntryListActivity extends FragmentActivity implements PasswordEntryListFragment.Callbacks {
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

        testDriveEncryption();

        IO.loadModel(this);
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            int fragmentId = R.id.passwordentry_detail_container;
            switchDetailFragment(id, fragmentId);
        } else {
            Intent detailIntent = new Intent(this, PasswordEntryDetailActivity.class);
            detailIntent.putExtra(Consts.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    // TODO: duplicating code in PasswordEntryDetailActivity
    private void switchDetailFragment(String id, int fragmentId) {
        Logger.i("bla", "switching fragment, item_id=%s, fragment_id=%s", id, fragmentId);
        Bundle arguments = new Bundle();
        arguments.putString(Consts.ARG_ITEM_ID, id);
        PasswordEntryDetailFragment fragment = new PasswordEntryDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(fragmentId, fragment).commit();
    }

    private String getItemIdFromIntent(Intent intent) {
        return Preconditions.checkNotNull(intent.getExtras().getString(Consts.ARG_ITEM_ID));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Logger.i("bla", "onResume, intent=%s", intent);
        if (intent != null) {
            if (Consts.EDIT_ACTION.equals(intent.getAction())) {
                switchDetailFragment(getItemIdFromIntent(intent), R.layout.fragment_passwordentry_detail_edit);
            } else if (Consts.VIEW_ACTION.equals(intent.getAction())) {
                switchDetailFragment(getItemIdFromIntent(intent), R.layout.fragment_passwordentry_detail);
            }
        }
        GoogleDriveUtil.authorize(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        IO.saveModel(this);
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
