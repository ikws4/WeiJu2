package io.ikws4.weiju;

import android.app.Application;

import io.ikws4.weiju.api.API;

public class WeiJu extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        API.initialize(this);
    }
}
