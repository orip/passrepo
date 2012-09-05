package com.example.passrepo;

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
            PasswordEntryDetailFragmentBase.switchDetailFragment(this, getIntent().getStringExtra(Consts.ARG_ITEM_ID), new PasswordEntryDetailFragment());
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
}
