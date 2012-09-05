package com.example.passrepo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class PasswordEntryDetailFragment extends PasswordEntryDetailFragmentBase {
    public PasswordEntryDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_passwordentry_detail, container, false);
        updateRootView(rootView);

        ((Button) rootView.findViewById(R.id.edit_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switchToDetailFragment(new PasswordEntryDetailEditFragment());
            }
        });

        return rootView;
    }
}
