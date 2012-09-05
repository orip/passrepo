package com.example.passrepo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.PasswordEntry;
import com.example.passrepo.util.Logger;
import com.google.common.base.Preconditions;

public class PasswordEntryDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    PasswordEntry mItem;

    private EditText title;

    private EditText password;

    private EditText userName;

    public PasswordEntryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = DummyContent.model.getPasswordEntry(getArguments().getString(ARG_ITEM_ID));
        }
    }

    private void switchToEditMode() {
        title.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        userName.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }
    
    private void switchToViewMode() {
        title.setInputType(InputType.TYPE_NULL);
        userName.setInputType(InputType.TYPE_NULL);
        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
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
        switchToViewMode();
        
        ((Button) rootView.findViewById(R.id.edit_button)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switchToEditMode();
            }
        });
        return rootView;
    }
}
