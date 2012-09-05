package com.example.passrepo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.PasswordEntry;
import com.example.passrepo.util.Logger;
import com.google.common.base.Preconditions;

public class PasswordEntryDetailEditFragment extends PasswordEntryDetailFragmentBase {
    public PasswordEntryDetailEditFragment() {
    }

    private void switchToViewMode() {
        switchToDetailFragment(new PasswordEntryDetailFragment());
    }

    private void saveEntry() {
        mItem.title = ((EditText) getView().findViewById(R.id.title)).getText().toString();
        mItem.userName = ((EditText) getView().findViewById(R.id.userName)).getText().toString();
        mItem.password = ((EditText) getView().findViewById(R.id.password)).getText().toString();
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("PasswordEntryDetailFragment", "mItem=%s", mItem);
        Preconditions.checkState(mItem != null);

        View rootView = inflater.inflate(R.layout.fragment_passwordentry_detail_edit, container, false);
        title = (EditText) rootView.findViewById(R.id.title);
        userName = (EditText) rootView.findViewById(R.id.userName);
        password = (EditText) rootView.findViewById(R.id.password);
        title.setText(mItem.title);
        userName.setText(mItem.userName);
        password.setText(mItem.password);

        ((Button) rootView.findViewById(R.id.cancel_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "No changes made", Toast.LENGTH_SHORT).show();
                switchToViewMode();
            }
        });
        ((Button) rootView.findViewById(R.id.update_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                saveEntry();
                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
                switchToViewMode();
            }
        });
        return rootView;
    }
}
