package io.ikws4.weiju.storage;

import android.content.Context;
import android.content.SharedPreferences;

class SharedPreferencesStoreStrategy implements StoreStrategy {
  private final SharedPreferences store;

  public SharedPreferencesStoreStrategy(Context context, int mode) {
    store = context.getSharedPreferences(STORE_NAME, mode);
  }

  @Override
  public String get(String k) {
    return store.getString(k, "");
  }

  @Override
  public void put(String k, String v) {
    store.edit().putString(k, v).apply();
  }
}
