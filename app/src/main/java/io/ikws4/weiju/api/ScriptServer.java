package io.ikws4.weiju.api;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ScriptServer {
    @GET("repos/ikws4/WeiJu2-Scripts/contents/scope_config.lua")
    Observable<GithubContentFile> getScopeConfig();

    @GET("repos/ikws4/WeiJu2-Scripts/contents/scripts/{name}.lua")
    Observable<GithubContentFile> getScript(@Path("name") String name);
}
