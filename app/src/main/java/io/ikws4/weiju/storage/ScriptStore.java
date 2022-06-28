package io.ikws4.weiju.storage;

import android.content.Context;

public class ScriptStore {
  private final StoreStrategy strategy;

  public ScriptStore(Context context) {
    if (isLSPEnabled(context)) {
      strategy = new WorldReadableStoreStrategy(context);
    } else {
      strategy = new RemoteStoreStrategy(context);
    }

    // Write a flag for xposed side to determine
    // XSharedPreferences is working or not
    put("flag", "1");
  }

  public String get(String k) {
    return strategy.get(k);
  }

  public void put(String k, String v) {
    strategy.put(k, v);
  }

  private boolean isLSPEnabled(Context context) {
    try {
      context.getSharedPreferences(StoreStrategy.STORE_NAME, Context.MODE_WORLD_READABLE);
    } catch (SecurityException e) {
      return true;
    }
    return false;
  }

  private static ScriptStore instance;

  public static ScriptStore getInstance(Context context) {
    if (instance == null) {
      instance = new ScriptStore(context);
    }
    return instance;
  }

}
