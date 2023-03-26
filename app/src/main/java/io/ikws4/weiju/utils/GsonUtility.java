package io.ikws4.weiju.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class GsonUtility {
    private static final Gson gson = new GsonBuilder().create();

    public static <T> T fromJson(String json, TypeToken<T> token) {
        return gson.fromJson(json, token);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }
}
