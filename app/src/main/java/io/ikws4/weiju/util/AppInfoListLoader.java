package io.ikws4.weiju.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ikws4.weiju.data.AppInfo;

public class AppInfoListLoader {
  private static final Map<CharSequence, Bitmap> ICON_CACHE = new HashMap<>();

  public static List<AppInfo> getUserApplications(Context context) {
    List<AppInfo> result = new ArrayList<>();
    PackageManager pm = context.getPackageManager();
    for (ApplicationInfo info : pm.getInstalledApplications(0)) {
      if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
        CharSequence name = info.loadLabel(pm);
        CharSequence pkg = info.packageName;
        Bitmap icon;
        if (ICON_CACHE.containsKey(pkg)) {
          icon = ICON_CACHE.get(pkg);
        } else {
          icon = loadIcon(context, info);
          ICON_CACHE.put(pkg, icon);
        }
        result.add(new AppInfo(name, pkg, icon));
      }
    }
    return result;
  }


  public static List<AppInfo> getApplications(Context context) {
    List<AppInfo> result = new ArrayList<>();
    PackageManager pm = context.getPackageManager();
    for (ApplicationInfo info : pm.getInstalledApplications(0)) {
      CharSequence name = info.loadLabel(pm);
      CharSequence pkg = info.packageName;
      Bitmap icon;
      if (ICON_CACHE.containsKey(pkg)) {
        icon = ICON_CACHE.get(pkg);
      } else {
        icon = loadIcon(context, info);
      }
      result.add(new AppInfo(name, pkg, icon));
    }
    return result;
  }

  private static Bitmap loadIcon(Context context, ApplicationInfo info) {
    File file = new File(context.getCacheDir(), info.packageName);
    Bitmap bitmap = null;
    try {
      if (file.exists()) {
        bitmap = BitmapFactory.decodeFile(file.getPath());
      } else {
        PackageManager pm = context.getPackageManager();

        Drawable icon = info.loadIcon(pm);
        if (icon instanceof BitmapDrawable) {
          bitmap = ((BitmapDrawable) icon).getBitmap();
        } else {
          int w = Math.max(1, icon.getIntrinsicWidth());
          int h = Math.max(1, icon.getIntrinsicHeight());
          bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
          icon.setBounds(0, 0, w, h);
          icon.draw(new Canvas(bitmap));
        }

        if (file.createNewFile()) {
          FileOutputStream out = new FileOutputStream(file);
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
          out.flush();
          out.close();
        } else {
          throw new IOException("Can't create new file.");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bitmap;
  }
}
