package io.ikws4.weiju.api.openai;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIApi {
    private final OpenAIServer mServer;
    private Gson mGson;

    public OpenAIApi(Context context) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .build();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        mGson = new Gson();
        mServer = retrofit.create(OpenAIServer.class);
    }

    public Observable<ChatResponse> chat(String apikey, String model, List<ChatResponse.Message> messages) {
        var map = new HashMap<String, Object>();
        map.put("model", model);
        map.put("messages", messages);
        map.put("stream", true);
        var body = mServer.chat("Bearer " + apikey, map);
        return body.flatMap((response) -> Observable.create(emitter -> {
            var souce = response.source();
            try {
                while (souce.isOpen() && !emitter.isDisposed() && !souce.exhausted()) {
                    var data = souce.readUtf8Line();
                    if (data.isEmpty()) {
                        continue;
                    }
                    data = data.substring("data: ".length());
                    if (data.equals("[DONE]")) {
                        emitter.onComplete();
                        break;
                    }
                    emitter.onNext(mGson.fromJson(data, TypeToken.get(ChatResponse.class)));
                }
            } catch (Exception e) {
                if (!emitter.isDisposed()) emitter.onError(e);
            }
        }));
    }
}
