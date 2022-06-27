package io.ikws4.weiju.storage;

import android.content.Context;

public class ScriptStorage {
  private final StorageStrategy strategy;

  public ScriptStorage(Context context) {
    if (isLSP(context)) {
      strategy = new LSPStorageStrategy(context);
    } else {
      strategy = new RemoteContentProviderStorageStrategy(context);
    }
  }

  public String read(String k) {
    return strategy.read(k);
  }

  public void write(String k, String v) {
    strategy.write(k, v);
  }

  private boolean isLSP(Context context) {
    try {
      context.getSharedPreferences(StorageStrategy.STORE_NAME, Context.MODE_WORLD_READABLE);
    } catch (SecurityException e) {
      return true;
    }
    return false;
  }

  private static ScriptStorage instance;

  public static ScriptStorage getInstance(Context context) {
    if (instance == null) {
      instance = new ScriptStorage(context);
    }
    return instance;
  }
}
