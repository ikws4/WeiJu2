package io.ikws4.weiju.xposed;

import android.util.Log;

public class Console {
    public static final String TAG = "Console";

    public static void printMsg(String msg) {
        Log.d(TAG, msg);
    }

    public static void printErr(String err) {
        Log.e(TAG, err);
    }

    public static void printErr(Throwable err) {
        Log.e(TAG, Log.getStackTraceString(err));
    }
}
