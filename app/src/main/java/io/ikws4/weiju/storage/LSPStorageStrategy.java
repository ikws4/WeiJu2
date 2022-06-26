package io.ikws4.weiju.storage;

import android.content.Context;
import android.content.SharedPreferences;

class LSPStorageStrategy implements StorageStrategy {
  private final SharedPreferences store;

  public LSPStorageStrategy(Context context) {
    store = context.getSharedPreferences(STORE_NAME, Context.MODE_WORLD_READABLE);
  }

  @Override
  public String read(String k) {
    return store.getString(k, "");
  }

  @Override
  public void write(String k, String v) {
    store.edit().putString(k, v).apply();
  }
}
