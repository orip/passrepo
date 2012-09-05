package com.example.passrepo;

import com.google.common.base.Preconditions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class PasswordEntryDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordentry_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            switchDetailFragment(getIntent().getStringExtra(Consts.ARG_ITEM_ID), R.id.passwordentry_detail_container);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, PasswordEntryListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    
    // TODO: duplicating code in PasswordEntryListActivity
    private void switchDetailFragment(String id, int fragmentId) {
        Bundle arguments = new Bundle();
        arguments.putString(Consts.ARG_ITEM_ID, id);
        PasswordEntryDetailFragment fragment = new PasswordEntryDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(fragmentId, fragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent != null) {
            if (Consts.EDIT_ACTION.equals(intent.getAction())) {
                switchDetailFragment(getItemIdFromIntent(intent), R.layout.fragment_passwordentry_detail_edit);
            } else if (Consts.VIEW_ACTION.equals(intent.getAction())) {
                switchDetailFragment(getItemIdFromIntent(intent), R.layout.fragment_passwordentry_detail);
            }
        }
    }

    private String getItemIdFromIntent(Intent intent) {
        return Preconditions.checkNotNull(intent.getExtras().getString(Consts.ITEM_ID_EXTRA));
    }
}
