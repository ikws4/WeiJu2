package io.ikws4.weiju.glide;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

class ApplicationIconDataFetcher implements DataFetcher<Drawable> {
    private final Context mContext;
    private final String mPkg;

    public ApplicationIconDataFetcher(Context context, String pkg) {
        mContext = context;
        mPkg = pkg;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Drawable> callback) {
        Drawable icon = null;
        try {
            icon = mContext.getPackageManager().getApplicationIcon(mPkg);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
