package io.ikws4.weiju.api;

import android.content.Context;

import io.ikws4.weiju.api.github.GithubAPI;
import io.ikws4.weiju.api.openai.OpenAIApi;

public class API {
    public static OpenAIApi OpenAIApi;
    public static GithubAPI GithubAPI;

    public static void initialize(Context context) {
        OpenAIApi = new OpenAIApi(context);
        GithubAPI = new GithubAPI(context);
    }
}
