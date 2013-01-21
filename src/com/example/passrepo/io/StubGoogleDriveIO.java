package com.example.passrepo.io;

import android.content.Context;

public class StubGoogleDriveIO implements GoogleDriveIO {
    public StubGoogleDriveIO(Context activity) {
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public void startSyncFromGoogleDriveToDisk(Runnable doneCallback) {
        doneCallback.run();
    }

    @Override
    public void saveModelAndStartSyncFromDiskToGoogleDrive(Runnable doneCallback) {
        doneCallback.run();
    }
}
