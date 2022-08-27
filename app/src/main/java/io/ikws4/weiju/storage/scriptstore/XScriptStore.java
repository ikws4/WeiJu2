package io.ikws4.weiju.storage.scriptstore;

import android.content.Context;

import java.util.Set;

import io.ikws4.weiju.storage.scriptstore.strategy.EmptyStoreStrategy;
import io.ikws4.weiju.storage.scriptstore.strategy.RemoteSharedPreferencesStoreStrategy;
import io.ikws4.weiju.storage.scriptstore.strategy.StoreStrategy;
import io.ikws4.weiju.storage.scriptstore.strategy.XSharedPreferenceStoreStrategy;
import io.ikws4.weiju.util.Logger;

public class XScriptStore {
    private StoreStrategy strategy;

    public XScriptStore(Context context) {
        if ((strategy = new XSharedPreferenceStoreStrategy()).canRead()) {
            Logger.d("XScriptStore:", "use XSharedPreferenceStoreStrategy");
            return;
        }

        if ((strategy = new RemoteSharedPreferencesStoreStrategy(context)).canRead()) {
            Logger.d("XScriptStore:", "use RemoteSharedPreferencesStoreStrategy");
            return;
        }

        this.strategy = new EmptyStoreStrategy();
        Logger.e("XScriptStore:", "can not load scripts.");
    }

    public static void fixPermission() {
        XSharedPreferenceStoreStrategy.fixPermission();
    }

    public boolean canRead() {
        return strategy.canRead();
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
