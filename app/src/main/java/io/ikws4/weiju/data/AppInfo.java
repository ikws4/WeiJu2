package io.ikws4.weiju.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class AppInfo {
  public CharSequence name;
  public CharSequence pkg;
  public Bitmap icon;

  public AppInfo(CharSequence name, CharSequence pkg, Bitmap icon) {
    this.name = name;
    this.pkg = pkg;
    this.icon = icon;
  }
}
