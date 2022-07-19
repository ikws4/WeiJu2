package io.ikws4.weiju.data;

import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;

import java.util.Objects;

public class AppInfo {
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

    public static boolean isSystemApp(ApplicationInfo info) {
        return (info.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
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
}
