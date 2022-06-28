package io.ikws4.weiju.storage;

import android.app.AndroidAppHelper;
import android.content.Context;

public class XScriptStore {
  private final StoreStrategy stragegy;

  public XScriptStore(Context context) {
    XposedStoreStrategy xposedStoreStrategy = new XposedStoreStrategy();
    if (xposedStoreStrategy.get("flag").equals("1")) {
      stragegy = xposedStoreStrategy;
    } else {
      stragegy = new RemoteStoreStrategy(context);
    }
  }

  public String get(String k) {
    return stragegy.get(k);
  }

  private static XScriptStore instance;

  public static XScriptStore getInstance() {
    if (instance == null) {
      Context context = AndroidAppHelper.currentApplication();
      instance = new XScriptStore(context);
    }
    return instance;
  }
}
