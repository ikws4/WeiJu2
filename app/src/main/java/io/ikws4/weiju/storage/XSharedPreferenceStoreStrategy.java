package io.ikws4.weiju.storage;

import de.robv.android.xposed.XSharedPreferences;
import io.ikws4.weiju.BuildConfig;

class XSharedPreferenceStoreStrategy implements StoreStrategy {
  private final XSharedPreferences store;

  public XSharedPreferenceStoreStrategy() {
    store = new XSharedPreferences(BuildConfig.APPLICATION_ID, STORE_NAME);
  }

  public boolean canRead() {
    return store.getFile().canRead();
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
