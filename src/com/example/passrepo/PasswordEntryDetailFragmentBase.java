package com.example.passrepo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.PasswordEntry;
import com.example.passrepo.util.Logger;
import com.google.common.base.Preconditions;

public abstract class PasswordEntryDetailFragmentBase extends Fragment {
    protected PasswordEntry mItem;

    public PasswordEntryDetailFragmentBase() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preconditions.checkState(getArguments().containsKey(Consts.ARG_ITEM_ID));
        mItem = DummyContent.model.getPasswordEntry(getArguments().getString(Consts.ARG_ITEM_ID));
    }

    protected void switchToDetailFragment(Fragment detailFragment) {
        Bundle arguments = new Bundle();
        arguments.putString(Consts.ARG_ITEM_ID, mItem.id);
        detailFragment.setArguments(arguments);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.passwordentry_detail_container, detailFragment).commit();
    }
    
    protected void updateRootView(View rootView) {
        Logger.i("PasswordEntryDetailFragment", "mItem=%s", mItem);
        Preconditions.checkState(mItem != null);

        ((TextView) rootView.findViewById(R.id.title)).setText(mItem.title);
        ((TextView) rootView.findViewById(R.id.userName)).setText(mItem.userName);
        ((TextView) rootView.findViewById(R.id.password)).setText(mItem.password);
    }
}
