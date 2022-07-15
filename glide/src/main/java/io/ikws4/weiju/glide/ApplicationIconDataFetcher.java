package io.ikws4.weiju.glide;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

class ApplicationIconDataFetcher implements DataFetcher<Drawable> {
    private final Context mContext;
    private final ApplicationInfo mInfo;

    public ApplicationIconDataFetcher(Context context, ApplicationInfo info) {
        mContext = context;
        mInfo = info;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        Drawable icon = mInfo.loadIcon(mContext.getPackageManager());
        callback.onDataReady(icon);
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<Drawable> getDataClass() {
        return Drawable.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}
