package io.ikws4.weiju.page.setting;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.ikws4.weiju.WeiJu;
import io.ikws4.weiju.page.BaseViewModel;

public class SettingViewModel extends BaseViewModel {
    private final String[] mDataXmls = new String[]{"script_store", "preference"};
    private final Gson mGson = new Gson();
    private final ClipboardManager mClipboardManager;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        mClipboardManager = WeiJu.getService(ClipboardManager.class);
    }

    public void backup() {
        Map<String, Object> data = new HashMap<>();
        for (var xml : mDataXmls) {
            SharedPreferences pref = getApplication().getSharedPreferences(xml, Context.MODE_PRIVATE);
            data.put(xml, pref.getAll());
        }
        var json = mGson.toJson(data);

        // save to the clipboard
        mClipboardManager.setPrimaryClip(ClipData.newPlainText("", json));
        Toast.makeText(getApplication(), "Saved to clipboard", Toast.LENGTH_SHORT).show();
    }

    public void restore() {
        // read from the clipboard
        var json = mClipboardManager.getPrimaryClip().getItemAt(0).getText().toString();

        // write to preferences
        try {
            Map<String, Object> data = mGson.fromJson(json, HashMap.class);
            for (var entry : data.entrySet()) {
                var xml = entry.getKey();

                if (!Arrays.stream(mDataXmls).anyMatch((it) -> it.equals(xml))) {
                    continue;
                }

                var content = (Map<String, Object>) entry.getValue();
                SharedPreferences pref = getApplication().getSharedPreferences(xml, Context.MODE_PRIVATE);
                var editor = pref.edit();
                editor.clear();
                for (var contentEntry : content.entrySet()) {
                    var key = contentEntry.getKey();
                    var value = contentEntry.getValue();
                    if (value instanceof Boolean) {
                        editor.putBoolean(key, (boolean) value);
                    } else if (value instanceof Integer) {
                        editor.putInt(key, (int) value);
                    } else if (value instanceof Long) {
                        editor.putLong(key, (long) value);
                    } else if (value instanceof Float) {
                        editor.putFloat(key, (float) value);
                    } else if (value instanceof String) {
                        editor.putString(key, (String) value);
                    } else if (value instanceof Set) {
                        editor.putStringSet(key, (Set<String>) value);
                    }
                }
                editor.apply();
            }
            Toast.makeText(getApplication(), "Restore succeed", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "Restore failed, please check your json data", Toast.LENGTH_LONG).show();
        }
    }
}
