package io.ikws4.weiju.storage.scriptstore.strategy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;
import com.crossbowffs.remotepreferences.RemotePreferences;

import java.nio.charset.StandardCharsets;
import java.util.Set;

public class RemoteSharedPreferencesStoreStrategy implements StoreStrategy {
    static final String AUTHORITY = "io.ikws4.weiju.storage";
    private final SharedPreferences store;

    public RemoteSharedPreferencesStoreStrategy(Context context) {
        store = new RemotePreferences(context, AUTHORITY, STORE_NAME);
    }

    @Override
    public boolean canRead() {
        return store.getBoolean(DUMMY_KEY, false);
    }

    @Override
    public String get(String k, String defValue) {
        if (!store.contains(k)) return defValue;
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

    public static class SharedPreferenceProvider extends RemotePreferenceProvider {
        public SharedPreferenceProvider() {
            super(AUTHORITY, new String[]{STORE_NAME});
        }
    }
}
