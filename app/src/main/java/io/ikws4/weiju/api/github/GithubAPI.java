package io.ikws4.weiju.api.github;

import android.content.Context;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubAPI {
    private final GithubScriptServer mServer;

    public GithubAPI(Context context) {
        int MB = 1 << 20;
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .cache(new Cache(context.getCacheDir(), 5 * MB))
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        mServer = retrofit.create(GithubScriptServer.class);
    }

    public Observable<GithubContentFile> getScopeConfig() {
        return mServer.getScopeConfig();
    }

    public Observable<GithubContentFile> getScript(String name) {
        return mServer.getScript(name);
    }

    private static GithubAPI instance;

    // public static GithubAPI getInstance() {
    //     if (instance == null) {
    //         throw new IllegalStateException("Not initialized.");
    //     }
    //     return instance;
    // }
    
    public static void initialize(Context context) {
        instance = new GithubAPI(context);
    }
}
