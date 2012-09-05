package com.example.passrepo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.PasswordEntry;
import com.example.passrepo.util.Logger;
import com.google.common.base.Preconditions;

public class PasswordEntryDetailFragment extends Fragment {
    // TODO: similar to PasswordEntryDetailEditFragment, extract common base class

    PasswordEntry mItem;

    private TextView title;

    private TextView password;

    private TextView userName;

    public PasswordEntryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preconditions.checkState(getArguments().containsKey(Consts.ARG_ITEM_ID));
        mItem = DummyContent.model.getPasswordEntry(getArguments().getString(Consts.ARG_ITEM_ID));
    }

    private void switchToEditMode() {
        getActivity().startActivity(
                new Intent(getActivity(), getActivity().getClass()).setAction(Consts.EDIT_ACTION).putExtra(Consts.ARG_ITEM_ID,
                        mItem.id).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("PasswordEntryDetailFragment", "mItem=%s", mItem);
        Preconditions.checkState(mItem != null);

        View rootView = inflater.inflate(R.layout.fragment_passwordentry_detail, container, false);
        title = (TextView) rootView.findViewById(R.id.title);
        userName = (TextView) rootView.findViewById(R.id.userName);
        password = (TextView) rootView.findViewById(R.id.password);
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
