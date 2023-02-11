package io.ikws4.weiju.storage.scriptstore.strategy;

import android.util.Base64;

import java.io.File;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import io.ikws4.weiju.BuildConfig;
import io.ikws4.weiju.util.Logger;

public class XSharedPreferenceStoreStrategy implements StoreStrategy {
    private static XSharedPreferences store;

    public XSharedPreferenceStoreStrategy() {
        if (XposedBridge.getXposedVersion() < 93) {
            store = new XSharedPreferences(new File("/data/user_de/0/" + BuildConfig.APPLICATION_ID + "/shared_prefs/" + STORE_NAME + ".xml"));
        } else {
            if (store == null) {
                /* store = new XSharedPreferences(BuildConfig.APPLICATION_ID, STORE_NAME); */
                Logger.e("Should call `XStore.fixPermission` in XposedInit.initZygote");
            } else {
                store.reload();
            }
        }
    }

    public static void fixPermission() {
        store = new XSharedPreferences(BuildConfig.APPLICATION_ID, STORE_NAME);
        store.makeWorldReadable();
        store.reload();
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
        throw new UnsupportedOperationException("read-only implementation");
    }

    @Override
    public void put(String k, Set<String> v) {
        throw new UnsupportedOperationException("read-only implementation");
    }
}
