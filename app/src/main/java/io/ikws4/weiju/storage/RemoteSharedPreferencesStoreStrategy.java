package io.ikws4.weiju.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;
import com.crossbowffs.remotepreferences.RemotePreferences;

public class RemoteSharedPreferencesStoreStrategy implements StoreStrategy {
  static final String AUTHORITY = "io.ikws4.weiju.storage";
  private final SharedPreferences store;

  public RemoteSharedPreferencesStoreStrategy(Context context) {
    store = new RemotePreferences(context, AUTHORITY, STORE_NAME);
  }

  @Override
  public String get(String k) {
    return store.getString(k, "");
  }

  @Override
  public void put(String k, String v) {
    store.edit().putString(k, v).apply();
  }

  public static class SharedPreferenceProvider extends RemotePreferenceProvider {
    public SharedPreferenceProvider() {
      super(AUTHORITY, new String[]{STORE_NAME});
    }
  }
}
