package io.ikws4.weiju.storage;

import android.content.Context;

public class Storage {
  private final StorageStrategy strategy;

  public Storage(Context context) {
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
      context.getSharedPreferences("store", Context.MODE_WORLD_READABLE);
    } catch (SecurityException e) {
      return true;
    }
    return false;
  }
}
