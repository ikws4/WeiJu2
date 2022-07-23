package io.ikws4.weiju.storage.strategy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class SharedPreferencesStoreStrategy implements StoreStrategy {
    private final SharedPreferences store;

    public SharedPreferencesStoreStrategy(Context context, int mode) {
        store = context.getSharedPreferences(STORE_NAME, mode);
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
        store.edit().putString(k, Base64.encodeToString(v.getBytes(StandardCharsets.UTF_8), 0)).apply();
    }

    @Override
    public void put(String k, Set<String> v) {
        store.edit().putStringSet(k, v).apply();
    }
}
