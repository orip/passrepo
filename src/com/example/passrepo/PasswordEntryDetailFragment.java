package com.example.passrepo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.passrepo.util.Logger;

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

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("bla", "detail view onResume");
        Intent intent = new Intent(getActivity(), CopyPasswordToClipboardActivity.class).putExtra(Consts.ARG_ITEM_ID, mItem.id);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(getActivity())
                .setContentTitle(String.format("Copy password for '%s'", mItem.title))
                .setContentText("Tap to copy password to clipboard").setAutoCancel(false).setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(true).setContentIntent(pendingIntent).setSubText(String.format("Username: '%s'", mItem.userName))
                .setSmallIcon(android.R.drawable.ic_input_add)
                .build();
        getNotificationManager().notify(Consts.COPY_PASSWORD_NOTIFICATION_ID, notification);
    }
    
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
