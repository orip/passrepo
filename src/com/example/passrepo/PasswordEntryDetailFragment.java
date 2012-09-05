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

public class PasswordEntryDetailFragment extends Fragment {
    // TODO: similar to PasswordEntryDetailEditFragment, extract common base class

    PasswordEntry mItem;

    private EditText title;

    private EditText password;

    private EditText userName;

    public PasswordEntryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preconditions.checkState(getArguments().containsKey(Consts.ARG_ITEM_ID));
        mItem = DummyContent.model.getPasswordEntry(getArguments().getString(Consts.ARG_ITEM_ID));
    }

    private void switchToEditMode() {
        getActivity().startActivity(new Intent(getActivity(), getActivity().getClass()).setAction(Consts.EDIT_ACTION).putExtra(Consts.ITEM_ID_EXTRA, mItem.id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("PasswordEntryDetailFragment", "mItem=%s", mItem);
        Preconditions.checkState(mItem != null);

        View rootView = inflater.inflate(R.layout.fragment_passwordentry_detail, container, false);
        title = (EditText) rootView.findViewById(R.id.title);
        userName = (EditText) rootView.findViewById(R.id.userName);
        password = (EditText) rootView.findViewById(R.id.password);
        title.setText(mItem.title);
        userName.setText(mItem.userName);
        password.setText(mItem.password);

        ((Button) rootView.findViewById(R.id.edit_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "No changes made", Toast.LENGTH_SHORT).show();
                switchToEditMode();
            }
        });

        return rootView;
    }
}
