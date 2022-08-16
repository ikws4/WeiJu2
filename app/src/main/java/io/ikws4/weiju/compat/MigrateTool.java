package io.ikws4.weiju.compat;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.ikws4.weiju.storage.Preferences;

/**
 * Help the user migrate from old WeiJu version
 */
public class MigrateTool {

  public static void migrate(Context context) {
    migrateHookListToAppList(context);
  }

  private static void migrateHookListToAppList(Context context) {
    SharedPreferences hookList = context.getSharedPreferences("hook_list", Context.MODE_PRIVATE);

    if (hookList.getAll().isEmpty()) {
      // Don't need migrate
      return;
    }

    // Copy app list
    Set<String> appList = new HashSet<>(Preferences.getInstance(context).get(Preferences.APP_LIST, Collections.emptySet()));
    String selectedPkg = null;
    for (String pkg : hookList.getAll().keySet()) {
      if (selectedPkg == null) {
        selectedPkg = pkg;
      }

      appList.add(pkg + "," + System.currentTimeMillis());
    }
    Preferences.getInstance(context).put(Preferences.APP_LIST, appList);

    // Select default app
    if (selectedPkg != null) {
      Preferences.getInstance(context).put(Preferences.APP_LIST_SELECTED_PACKAGE, selectedPkg);
    }

    // Clear old data
    hookList.edit().clear().apply();
  }
}
