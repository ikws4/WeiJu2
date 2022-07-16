package io.ikws4.weiju.data;

public class AppInfo {
  public final CharSequence name;
  public final CharSequence pkg;
  public final String imgUri;

  public AppInfo(CharSequence name, CharSequence pkg) {
    this.name = name;
    this.pkg = pkg;
    this.imgUri = "pkg:" + pkg;
  }
}
