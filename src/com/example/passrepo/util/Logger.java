package com.example.passrepo.util;

import android.util.Log;

public class Logger {
    public static void i(String tag, String format, Object...args) {
        Log.i(tag, String.format(format, args));
    }
}
