package com.example.passrepo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.passrepo.model.Model;
import com.example.passrepo.model.PasswordEntry;
import com.google.common.base.Preconditions;

public class CopyPasswordToClipboardActivity extends Activity {
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (intent != null) {
            String itemId = Preconditions.checkNotNull(intent.getExtras().getString(Consts.ARG_ITEM_ID));
            PasswordEntry item = Model.currentModel.getPasswordEntry(itemId);
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText(String.format("password for '%s'", item.title), item.password));
            Toast.makeText(this, "Copied password to clipboard", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Error copying to clipboard", Toast.LENGTH_LONG).show();
        }
        finish();
    }
}
