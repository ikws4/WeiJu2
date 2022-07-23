package io.ikws4.weiju.storage.strategy;

import android.util.Base64;

import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;
import io.ikws4.weiju.BuildConfig;

public class XSharedPreferenceStoreStrategy implements StoreStrategy {
  private final XSharedPreferences store;

  public XSharedPreferenceStoreStrategy() {
    store = new XSharedPreferences(BuildConfig.APPLICATION_ID, STORE_NAME);
  }

  public boolean canRead() {
    return store.getFile().canRead();
  }

  @Override
  public String get(String k, String defValue) {
    String v = store.getString(k, defValue);
    return new String(Base64.decode(v, 0));
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
