package io.ikws4.weiju.glide;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

public class ApplicationIconModelLoader implements ModelLoader<ApplicationInfo, Drawable> {
    private final Context mContext;

    public ApplicationIconModelLoader(Context context) {
        mContext = context;
    }

    @Override
    public LoadData<Drawable> buildLoadData(@NonNull ApplicationInfo info, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(info), new ApplicationIconDataFetcher(mContext, info));
    }

    @Override
    public boolean handles(@NonNull ApplicationInfo info) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<ApplicationInfo, Drawable> {
        private final Context mContext;

        public Factory(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public ModelLoader<ApplicationInfo, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ApplicationIconModelLoader(mContext);
        }

        @Override
        public void teardown() {

        }
    }

}
