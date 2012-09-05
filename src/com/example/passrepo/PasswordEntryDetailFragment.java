package com.example.passrepo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.PasswordEntry;
import com.example.passrepo.util.Logger;

public class PasswordEntryDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    PasswordEntry mItem;

    public PasswordEntryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = DummyContent.model.getPasswordEntry(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_passwordentry_detail, container, false);
        Logger.i("PasswordEntryDetailFragment", "mItem=%s", mItem);
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.title)).setText(mItem.title);
            ((TextView) rootView.findViewById(R.id.userName)).setText(mItem.userName);
            ((TextView) rootView.findViewById(R.id.password)).setText(mItem.password);
        }
        return rootView;
    }
}
