package com.example.passrepo;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.io.IO;
import com.example.passrepo.io.StubGoogleDriveIO;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;

public class PasswordEntryListActivity extends FragmentActivity implements PasswordEntryListFragment.Callbacks {
    private boolean mTwoPane;

    private static final boolean TEST_ENCRYPTION = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        testDriveEncryption();
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            PasswordEntryDetailFragmentBase.switchDetailFragment(this, id, new PasswordEntryDetailFragment());
        } else {
            Intent detailIntent = new Intent(this, PasswordEntryDetailActivity.class);
            detailIntent.putExtra(Consts.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Consts.COPY_PASSWORD_NOTIFICATION_ID);
    }

    protected void onResume() {
        super.onResume();
        testDriveEncryption();

        // Once we have an updated model, set the passwords list content view. Avoid it if the list is already set.
        if (Model.currentModel != null && findViewById(android.R.id.content) != null) {
            setContentView(R.layout.activity_passwordentry_list);
            if (findViewById(R.id.passwordentry_detail_container) != null) {
                mTwoPane = true;
                ((PasswordEntryListFragment) getSupportFragmentManager().findFragmentById(R.id.passwordentry_list))
                        .setActivateOnItemClick(true);
            }
            
        } else {
            final ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage("Loading Passwords..");
            loadingDialog.setCancelable(false);
            loadingDialog.show();
            
            new StubGoogleDriveIO(this).startSyncFromGoogleDriveToDisk(new Runnable() {
                @Override
                public void run() {
                    Logger.i("PasswordEntryListActivity", "Done Syncing from Drive, loading model from disk..");
                    IO.loadModelFromDisk(PasswordEntryListActivity.this);

                    Logger.i("PasswordEntryListActivity", "Upadting UI..");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            loadingDialog.dismiss();
                            startActivity(getIntent());
                            finish();
                        }
                    });
                }
            });
        }
    }

    @SuppressWarnings("unused")
    private void testDriveEncryption() {
        if (!TEST_ENCRYPTION)
            return;
        String encryptedString = IO.modelToEncryptedString(DummyContent.model);
        Model decryptedModel = IO.modelFromEncryptedString(encryptedString, DummyContent.model.key);
        Logger.i("TEST", "originalModel=%s", GsonHelper.customGson.toJson(DummyContent.model));
        Logger.i("TEST", "encryptedString=%s", encryptedString);
        Logger.i("TEST", "decryptedModel=%s", GsonHelper.customGson.toJson(decryptedModel));

        if (false) {
            ScryptParameters scryptParameters = new ScryptParameters(new byte[]{});
            byte[] key = PasswordHasher.hash("foo", scryptParameters);
            CipherText encrypted = Encryption.encrypt("What's up man?".getBytes(Charsets.UTF_8), key);
            String decrypted = new String(Encryption.decrypt(encrypted, key), Charsets.UTF_8);
            Logger.i("TEST", "derivedKey=%s", Base64.encodeToString(key, Base64.NO_WRAP));
            Logger.i("TEST", "encrypted=%s", Base64.encodeToString(encrypted.bytes, Base64.NO_WRAP));
            Logger.i("TEST", "decrypted=%s", decrypted);
        }
    }

    private static final int MENU_CHANGE_PASSWORD = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_CHANGE_PASSWORD, Menu.NONE, R.string.change_password_menu_label).setIcon(
                android.R.drawable.ic_menu_agenda);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_CHANGE_PASSWORD:
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.change_password_alert_dialog, null);

            new AlertDialog.Builder(this).setView(textEntryView).setPositiveButton("Update", new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String password = ((EditText) textEntryView.findViewById(R.id.password_entry_1)).getText().toString();
                    Model.currentModel.key = PasswordHasher.hash(password, Model.currentModel.scryptParameters);
                    new StubGoogleDriveIO(PasswordEntryListActivity.this).saveModelAndStartSyncFromDiskToGoogleDrive(new Runnable() {
                        public void run() {
                            PasswordEntryListActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(PasswordEntryListActivity.this, "Password updated", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }).setNegativeButton("Not now", null).setCancelable(true).setOnCancelListener(null).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
