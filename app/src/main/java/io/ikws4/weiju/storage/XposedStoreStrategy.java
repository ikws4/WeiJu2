package io.ikws4.weiju.storage;

import android.content.SharedPreferences;

import de.robv.android.xposed.XSharedPreferences;
import io.ikws4.weiju.BuildConfig;

class XposedStoreStrategy implements StoreStrategy {
  private final SharedPreferences store;

  public XposedStoreStrategy() {
    store = new XSharedPreferences(BuildConfig.APPLICATION_ID, STORE_NAME);
  }

  @Override
  public String get(String k) {
    return store.getString(k, "");
  }

  @Override
  public void put(String k, String v) {
    throw new UnsupportedOperationException("read-only implementation");
  }
}
