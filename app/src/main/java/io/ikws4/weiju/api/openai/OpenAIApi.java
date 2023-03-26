package io.ikws4.weiju.api.openai;

import android.content.Context;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIApi {
    private final OpenAIServer mServer;

    public OpenAIApi(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        mServer = retrofit.create(OpenAIServer.class);
    }

    public Observable<ChatCompletion> chat(String apikey, List<ChatCompletion.Message> messages) {
        return mServer.chat("Bearer " + apikey, messages);
    }
}
