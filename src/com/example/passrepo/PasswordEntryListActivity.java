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
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;
import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.events.SearchQueryUpdatedEvent;
import com.example.passrepo.io.IO;
import com.example.passrepo.io.StubGoogleDriveIO;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.squareup.otto.Bus;

public class PasswordEntryListActivity extends FragmentActivity implements PasswordEntryListFragment.Callbacks, SearchView.OnQueryTextListener {
    private boolean mTwoPane;
    private SearchView mSearchView;

    private static final boolean TEST_ENCRYPTION = false;

    private final Bus bus;

    public PasswordEntryListActivity() {
        this.bus = BusWrapper.globalBus;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testDriveEncryption();
        initActivity();
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            findViewById(R.id.passwordentry_detail_container).setVisibility(View.VISIBLE);
            PasswordEntryDetailFragmentBase.switchDetailFragment(this, id, new PasswordEntryDetailFragment());

            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(findViewById(R.id.searchView).getWindowToken(), 0);

        } else {
            Intent detailIntent = new Intent(this, PasswordEntryDetailActivity.class);
            detailIntent.putExtra(Consts.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IO.saveModelToDisk(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(Consts.COPY_PASSWORD_NOTIFICATION_ID);
    }

    protected void onResume() {
        super.onResume();
        testDriveEncryption();
    }

    private void initActivity() {
        // Once we have an updated model, set the passwords list content view. Avoid it if the list is already set.
        if (Model.currentModel != null && findViewById(android.R.id.content) != null) {
            setContentView(R.layout.activity_passwordentry_list);
            if (findViewById(R.id.passwordentry_detail_container) != null) {
                mTwoPane = true;
                ((PasswordEntryListFragment) getSupportFragmentManager().findFragmentById(R.id.passwordentry_list))
                        .setActivateOnItemClick(true);
            }

        } else {
            // TODO: probably belongs to onResume
            final ProgressDialog loadingDialog = new ProgressDialog(this);
            loadingDialog.setMessage("Loading Passwords..");
            loadingDialog.setCancelable(false);
            loadingDialog.show();

            new StubGoogleDriveIO(this).startSyncFromGoogleDriveToDisk(new Runnable() {
                @Override
                public void run() {
                    Logger.i("PasswordEntryListActivity", "Done Syncing from Drive, loading model from disk..");
                    IO.loadModelFromDisk(PasswordEntryListActivity.this);

                    Logger.i("PasswordEntryListActivity", "Updating UI..");
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
        Model decryptedModel = null;
        try {
            decryptedModel = IO.modelFromEncryptedString(encryptedString, DummyContent.model.keys);
        } catch (PassRepoBaseSecurityException e) {
            throw new RuntimeException(e);
        }
        Logger.i("TEST", "originalModel=%s", GsonHelper.customGson.toJson(DummyContent.model));
        Logger.i("TEST", "encryptedString=%s", encryptedString);
        Logger.i("TEST", "decryptedModel=%s", GsonHelper.customGson.toJson(decryptedModel));

        if (false) {
            ScryptParameters scryptParameters = new ScryptParameters(new byte[]{});
            final PasswordHasher.Keys keys = PasswordHasher.hash("foo", scryptParameters);
            CipherText encrypted = Encryption.encrypt("What's up man?".getBytes(Charsets.UTF_8), keys);
            String decrypted = new String(Encryption.decrypt(encrypted, keys.encryptionKey), Charsets.UTF_8);
            Logger.i("TEST", "derivedKey=%s", Base64.encodeToString(keys.encryptionKey, Base64.NO_WRAP));
            Logger.i("TEST", "encrypted=%s", Base64.encodeToString(encrypted.bytes, Base64.NO_WRAP));
            Logger.i("TEST", "decrypted=%s", decrypted);
        }
    }

    private static final int MENU_CHANGE_PASSWORD = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_CHANGE_PASSWORD, Menu.NONE, R.string.change_password_menu_label).setIcon(
                android.R.drawable.ic_menu_agenda);
        super.onCreateOptionsMenu(menu);

        if (!mTwoPane) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.searchview_in_menu, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            mSearchView = (SearchView) searchItem.getActionView();
            setupSearchView(searchItem);
        }

        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        if (searchManager != null) {
//            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();
//
//            // Try to use the "applications" global search provider
//            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
//            for (SearchableInfo inf : searchables) {
//                if (inf.getSuggestAuthority() != null
//                        && inf.getSuggestAuthority().startsWith("applications")) {
//                    info = inf;
//                }
//            }
//            mSearchView.setSearchableInfo(info);
//        }

        mSearchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        bus.post(new SearchQueryUpdatedEvent(newText));
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        // do nothing, the correct results should be available from the realtime text updates
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
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
                        final PasswordHasher.Keys keys = PasswordHasher.hash(password, Model.currentModel.scryptParameters);
                        Model.currentModel.keys = keys;
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
