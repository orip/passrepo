package com.example.passrepo.io;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GlobalExecutors {
    public static final Executor BACKGROUND_PARALLEL_EXECUTOR = Executors.newCachedThreadPool();

    // Inspired by https://github.com/square/retrofit/pull/90
    public static final Executor MAIN_THREAD_EXECUTOR = new Executor() {
        private final Handler handler = new Handler(Looper.getMainLooper());

        public void execute(Runnable command) {
            handler.post(command);
        }
    };
}
