package io.ikws4.weiju.sdk;

import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.WeiJu;

public class FirebaseSdk {
    public static void initialize(Context context) {
        initCrashlytics();
    }

    private static void initCrashlytics() {
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setCustomKey("commit_hash", BuildConfig.COMMIT_HASH);
        crashlytics.setCustomKey("xposed_enabled", WeiJu.XPOSED_ENABLED);
    }
}
