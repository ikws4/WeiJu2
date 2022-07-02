package io.ikws4.weiju.util;

import android.util.Log;

public class Logger {
    private static final String TAG = "WeiJu";
    private static final StringBuilder sb = new StringBuilder();

    public static void d(String tag, Object... msgs) {
        pack(msgs);
        Log.d(TAG, sb.toString());
    }

    public static void e(Object... msgs) {
        pack(msgs);
        Log.e(TAG, sb.toString());
    }

    public static void e(Throwable t) {
        Log.e(TAG, Log.getStackTraceString(t));
    }

    private static void pack(Object... msgs) {
        sb.setLength(0);
        for (Object msg : msgs) {
            sb.append(msg).append(" ");
        }
    }
}
