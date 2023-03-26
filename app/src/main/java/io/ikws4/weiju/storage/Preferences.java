package io.ikws4.weiju.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Set;

public class Preferences {
  private static final String STORE_NAME = "preference";
  private final SharedPreferences storage;

  /// Keys
  public static final String APP_LIST_SELECTED_PACKAGE = "app_list_selected_package";
  public static final String APP_LIST = "app_list";
  public static final String LOGCAT_TIME = "logcat_time";
  public static final String PACKAGE_LIST_SUFFIX = "_package_list";
  public static final String AUTHOR = "author";
  public static final String OPENAI_API_KEY = "openai_api_key";
  public static final String OPENAI_CHAT_MODEL = "openai_chat_model";

  private Preferences(Context context) {
    storage = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE);
  }

  @Nullable
  public String get(String key, @Nullable String defValue) {
    return storage.getString(key, defValue);
  }

  public int get(String key, int defValue) {
    return storage.getInt(key, defValue);
  }

  public long get(String key, long defValue) {
    return storage.getLong(key, defValue);
  }

  public float get(String key, float defValue) {
    return storage.getFloat(key, defValue);
  }

  public boolean get(String key, boolean defValue) {
    return storage.getBoolean(key, defValue);
  }

  public Set<String> get(String key, Set<String> defValue) {
    return storage.getStringSet(key, defValue);
  }

  public boolean contains(String key) {
    return storage.contains(key);
  }

  public void put(String key, @Nullable String value) {
    storage.edit().putString(key, value).apply();
  }

  public void put(String key, int value) {
    storage.edit().putInt(key, value).apply();
  }

  public void put(String key, long value) {
    storage.edit().putLong(key, value).apply();
  }

  public void put(String key, float value) {
    storage.edit().putFloat(key, value).apply();
  }

  public void put(String key, boolean value) {
    storage.edit().putBoolean(key, value).apply();
  }

  public void put(String key, Set<String> value) {
    storage.edit().putStringSet(key, value).apply();
  }

  public void remove(String key) {
    storage.edit().remove(key).apply();
  }

  public void clear() {
    storage.edit().clear().apply();
  }

  private static Preferences instance;

  public static Preferences getInstance(Context context) {
    if (instance == null) {
      instance = new Preferences(context);
    }
    return instance;
  }

}
