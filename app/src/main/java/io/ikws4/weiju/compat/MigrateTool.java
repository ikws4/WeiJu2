package io.ikws4.weiju.compat;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.ikws4.weiju.page.home.widget.ScriptListView;
import io.ikws4.weiju.storage.Preferences;
import io.ikws4.weiju.storage.ScriptStore;
import io.ikws4.weiju.util.Logger;
import io.ikws4.weiju.util.Strings;
import io.ikws4.weiju.util.Template;

/**
 * Help the user migrate from old WeiJu version
 */
public class MigrateTool {

    public static void migrate(Context context) {
        migrateHookList(context);
    }

    private static void migrateHookList(Context context) {
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

            SharedPreferences sp = context.getSharedPreferences(pkg, Context.MODE_PRIVATE);
            try {
                Template t = new Template(context.getAssets().open("migrate_init_template"));
                migrateStatusBar(sp, t);
                migrateNavBar(sp, t);
                migrateScreen(sp, t);
                migrateVariable(sp, t);

                // Add migrate_init form this app
                ScriptStore scriptStore = ScriptStore.getInstance(context);
                ScriptListView.ScriptItem item = ScriptListView.ScriptItem.from(t.toString());
                String key = Strings.join("_", pkg, item.id);

                Set<String> keys = new HashSet<>(scriptStore.get(pkg, Collections.emptySet()));
                keys.add(key);

                scriptStore.put(pkg, keys);
                scriptStore.put(key, item.script);

                // Clear old data
                sp.edit().clear().apply();
            } catch (IOException e) {
                Logger.e(e);
            }
        }
        Preferences.getInstance(context).put(Preferences.APP_LIST, appList);

        // Select default app
        if (selectedPkg != null) {
            Preferences.getInstance(context).put(Preferences.APP_LIST_SELECTED_PACKAGE, selectedPkg);
        }

        // Clear old data
        hookList.edit().clear().apply();
    }

    private static void migrateStatusBar(SharedPreferences sp, Template t) {
        tSetBoolean(t, sp, "is_enable_status_bar");
        tSetBoolean(t, sp, "is_hide_status_bar");
        tSetString(t, sp, "immersive_status_bar");
        tSetString(t, sp, "custom_status_bar_color");
        tSetString(t, sp, "status_bar_icon_color");
    }

    private static void migrateNavBar(SharedPreferences sp, Template t) {
        tSetBoolean(t, sp, "is_enable_nav_bar");
        tSetBoolean(t, sp, "is_hide_nav_bar");
        tSetString(t, sp, "immersive_nav_bar");
        tSetString(t, sp, "custom_nav_bar_color");
        tSetString(t, sp, "nav_bar_icon_color");
    }

    private static void migrateScreen(SharedPreferences sp, Template t) {
        tSetBoolean(t, sp, "is_enable_screen");
        tSetInt(t, sp, "screen_orientation");
        tSetBoolean(t, sp, "is_enable_force_screenshot");
        tSetBoolean(t, sp, "is_cancel_dialog");
        tSetString(t, sp, "language");
        tSetInt(t, sp, "custom_dpi");
    }

    private static void migrateVariable(SharedPreferences sp, Template t) {
        tSetBoolean(t, sp, "is_enable_variable");
        tSetString(t, sp, "variable_device");
        tSetString(t, sp, "variable_product_name");
        tSetString(t, sp, "variable_model");
        tSetString(t, sp, "variable_brand");
        tSetString(t, sp, "variable_release");
        tSetDouble(t, sp, "variable_longitude");
        tSetDouble(t, sp, "variable_latitude");
        tSetString(t, sp, "variable_imei");
        tSetString(t, sp, "variable_imsi");
    }

    private static void tSetBoolean(Template t, SharedPreferences sp, String key) {
        t.set(key, String.valueOf(sp.getBoolean(key, false)));
    }

    private static void tSetString(Template t, SharedPreferences sp, String key) {
        t.set(key, spGetString(sp, key));
    }

    private static void tSetInt(Template t, SharedPreferences sp, String key) {
        t.set(key, spGetStringWithouQuotes(sp, key));
    }

    private static void tSetDouble(Template t, SharedPreferences sp, String key) {
        t.set(key, spGetStringWithouQuotes(sp, key));
    }

    private static String spGetString(SharedPreferences sp, String key) {
        String value = spGetStringWithouQuotes(sp, key);
        if (value.equals("nil")) return value;
        return "\"" + value + "\"";
    }

    private static String spGetStringWithouQuotes(SharedPreferences sp, String key) {
        String value = sp.getString(key, "");
        if (value.equals("") || value.equals("Default") || value.equals("error: not permission") || value.equals("error: failure")) {
            return "nil";
        }
        return value;
    }
}
