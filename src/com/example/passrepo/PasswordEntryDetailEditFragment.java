package com.example.passrepo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.passrepo.io.IO;

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
        IO.saveModel(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_passwordentry_detail_edit, container, false);
        updateRootView(rootView);

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
