package io.ikws4.weiju.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

public class ApplicationIconModelLoader implements ModelLoader<String, Drawable> {
    private static final String PREFIX = "pkg:";

    private final Context mContext;

    public ApplicationIconModelLoader(Context context) {
        mContext = context;
    }

    @Override
    public LoadData<Drawable> buildLoadData(@NonNull String s, int width, int height, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(s), new ApplicationIconDataFetcher(mContext, s.substring(PREFIX.length())));
    }

    @Override
    public boolean handles(@NonNull String s) {
        return s.startsWith(PREFIX);
    }

    public static class Factory implements ModelLoaderFactory<String, Drawable> {
        private final Context mContext;

        public Factory(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public ModelLoader<String, Drawable> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new ApplicationIconModelLoader(mContext);
        }

        @Override
        public void teardown() {

        }
    }

}
