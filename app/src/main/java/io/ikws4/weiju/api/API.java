package io.ikws4.weiju.api;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {
    private final ScriptServer mServer;

    public API() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        mServer = retrofit.create(ScriptServer.class);
    }

    public Observable<GithubContentFile> getScopeConfig() {
        return mServer.getScopeConfig();
    }

    public Observable<GithubContentFile> getScript(String name) {
        return mServer.getScript(name);
    }

    private static API instance;

    public static API getInstance() {
        if (instance == null) {
            instance = new API();
        }
        return instance;
    }
}
