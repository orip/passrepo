package com.example.passrepo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class PasswordEntryListActivity extends FragmentActivity
        implements PasswordEntryListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordentry_list);

        if (findViewById(R.id.passwordentry_detail_container) != null) {
            mTwoPane = true;
            ((PasswordEntryListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.passwordentry_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(PasswordEntryDetailFragment.ARG_ITEM_ID, id);
            PasswordEntryDetailFragment fragment = new PasswordEntryDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.passwordentry_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, PasswordEntryDetailActivity.class);
            detailIntent.putExtra(PasswordEntryDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
