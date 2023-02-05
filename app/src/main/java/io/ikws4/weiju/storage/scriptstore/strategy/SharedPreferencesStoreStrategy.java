package io.ikws4.weiju.storage.scriptstore.strategy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class SharedPreferencesStoreStrategy implements StoreStrategy {
    private final SharedPreferences store;

    public SharedPreferencesStoreStrategy(Context context, int mode) {
        store = context.getSharedPreferences(STORE_NAME, mode);
        store.edit().putBoolean(DUMMY_KEY, true).apply();
    }

    @Override
    public boolean canRead() {
        return store.getBoolean(DUMMY_KEY, false);
    }

    @Override
    public String get(String k, String defValue) {
        return new String(Base64.decode(store.getString(k, defValue), 0));
    }

    @Override
    public Set<String> get(String key, Set<String> defValue) {
        return store.getStringSet(key, defValue);
    }

    @Override
    public void put(String k, String v) {
        if (v.isEmpty()) {
            v = null;
        } else {
            v = Base64.encodeToString(v.getBytes(StandardCharsets.UTF_8), 0);
        }
        store.edit().putString(k, v).apply();
    }

    @Override
    public void put(String k, Set<String> v) {
        if (v.isEmpty()) {
            v = null;
        }
        store.edit().putStringSet(k, v).apply();
    }
}
