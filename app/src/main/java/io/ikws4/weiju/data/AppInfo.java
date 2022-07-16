package io.ikws4.weiju.data;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AppInfo {
  public final CharSequence name;
  public final CharSequence pkg;
  public final String imgUri;

  public AppInfo(@NonNull CharSequence name, @NonNull CharSequence pkg) {
    this.name = name;
    this.pkg = pkg;
    this.imgUri = "pkg:" + pkg;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AppInfo info = (AppInfo) o;

    if (!Objects.equals(name, info.name)) return false;
    if (!Objects.equals(pkg, info.pkg)) return false;
    return Objects.equals(imgUri, info.imgUri);
  }

  @Override
  public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + pkg.hashCode();
    result = 31 * result + imgUri.hashCode();
    return result;
  }
}
