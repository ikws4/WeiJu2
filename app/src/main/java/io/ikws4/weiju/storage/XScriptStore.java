package io.ikws4.weiju.storage;

import android.content.Context;
import android.os.Build;

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

    public String get(String k) {
        return strategy.get(k);
    }

    private static XScriptStore instance;

    public static XScriptStore getInstance(Context context) {
        if (instance == null) {
            instance = new XScriptStore(context);
        }
        return instance;
    }
}
