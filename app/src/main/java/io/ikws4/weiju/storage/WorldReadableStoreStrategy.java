package io.ikws4.weiju.storage;

import android.content.Context;
import android.content.SharedPreferences;

class WorldReadableStoreStrategy implements StoreStrategy {
  private final SharedPreferences store;

  public WorldReadableStoreStrategy(Context context) {
    store = context.getSharedPreferences(STORE_NAME, Context.MODE_WORLD_READABLE);
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
