package io.ikws4.weiju.storage;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

import io.ikws4.weiju.util.Logger;

public class ScriptStore {
    private final StoreStrategy strategy;

    public ScriptStore(Context context) {
        if (isLSPEnabled(context)) {
            strategy = new SharedPreferencesStoreStrategy(context, Context.MODE_WORLD_READABLE);
            Logger.d("ScriptStore:", "use SharedPreferencesStoreStrategy(MODE_WORLD_READABLE)");
        } else {
            strategy = new SharedPreferencesStoreStrategy(context, Context.MODE_PRIVATE);
            Logger.d("ScriptStore:", "use SharedPreferencesStoreStrategy(MODE_PRIVATE)");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                new AlertDialog.Builder(context)
                    .setTitle("Warning")
                    .setMessage("WeiJu may not wroking in your device, could you try using LSPosed instead?")
                    .show();
            }
        }
    }

    public String get(String k) {
        return strategy.get(k);
    }

    public void put(String k, String v) {
        strategy.put(k, v);
    }

    private boolean isLSPEnabled(Context context) {
        try {
            context.getSharedPreferences(StoreStrategy.STORE_NAME, Context.MODE_WORLD_READABLE);
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    private static ScriptStore instance;

    public static ScriptStore getInstance(Context context) {
        if (instance == null) {
            instance = new ScriptStore(context);
        }
        return instance;
    }

}
