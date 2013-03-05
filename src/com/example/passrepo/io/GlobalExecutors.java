package com.example.passrepo.io;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GlobalExecutors {
    public static final Executor CACHED_EXECUTOR = Executors.newCachedThreadPool();
}
