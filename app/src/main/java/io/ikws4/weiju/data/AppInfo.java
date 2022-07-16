package io.ikws4.weiju.data;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AppInfo implements Comparable<AppInfo>{
    public final String name;
    public final String pkg;
    public final String imgUri;
    public final boolean isSystemApp;

    public AppInfo(@NonNull String name, @NonNull String pkg, boolean isSystemApp) {
        this.name = name;
        this.pkg = pkg;
        this.imgUri = "pkg:" + pkg;
        this.isSystemApp = isSystemApp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppInfo info = (AppInfo) o;
        return isSystemApp == info.isSystemApp && Objects.equals(name, info.name) && Objects.equals(pkg, info.pkg) && Objects.equals(imgUri, info.imgUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pkg, imgUri, isSystemApp);
    }

    @Override
    public int compareTo(AppInfo o) {
        int res = Boolean.compare(isSystemApp, o.isSystemApp) ;
        if (res == 0) {
            return pkg.compareTo(o.pkg);
        }
        return res;
    }
}
