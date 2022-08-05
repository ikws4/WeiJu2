package io.ikws4.weiju.storage;

import android.content.Context;
import android.os.Build;

import java.util.Set;

import io.ikws4.weiju.storage.strategy.EmptyStoreStrategy;
import io.ikws4.weiju.storage.strategy.RemoteSharedPreferencesStoreStrategy;
import io.ikws4.weiju.storage.strategy.StoreStrategy;
import io.ikws4.weiju.storage.strategy.XSharedPreferenceStoreStrategy;
import io.ikws4.weiju.util.Logger;

public class XScriptStore {
    private final StoreStrategy strategy;

    public XScriptStore(Context context) {
        XSharedPreferenceStoreStrategy strategy = new XSharedPreferenceStoreStrategy();
        if (strategy.canRead()) {
            this.strategy = strategy;
            Logger.d("XScriptStore:", "use XSharedPreferenceStoreStrategy");
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            this.strategy = new RemoteSharedPreferencesStoreStrategy(context);
            Logger.d("XScriptStore:", "use RemoteSharedPreferencesStoreStrategy");
        } else {
            this.strategy = new EmptyStoreStrategy();
            Logger.e("XScriptStore:", "can not load scripts.");
        }
    }

    public static void fixPermission() {
        XSharedPreferenceStoreStrategy.fixPermission();
    }

    public String get(String k, String defValue) {
        return strategy.get(k, defValue);
    }

    public Set<String> get(String key, Set<String> defValue) {
        return strategy.get(key, defValue);
    }

    private static XScriptStore instance;

    public static XScriptStore getInstance(Context context) {
        if (instance == null) {
            instance = new XScriptStore(context);
        }
        return instance;
    }
}
