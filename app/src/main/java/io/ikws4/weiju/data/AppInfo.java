package io.ikws4.weiju.data;

import android.content.pm.ApplicationInfo;

public class AppInfo {
  public CharSequence name;
  public CharSequence pkg;
  public ApplicationInfo info;

  public AppInfo(CharSequence name, CharSequence pkg, ApplicationInfo info) {
    this.name = name;
    this.pkg = pkg;
    this.info = info;
  }
}
