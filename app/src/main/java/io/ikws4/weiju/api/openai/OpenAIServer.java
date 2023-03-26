package io.ikws4.weiju.api.openai;


import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface OpenAIServer {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    @Streaming
    Observable<ResponseBody> chat(
        @Header("Authorization") String schemeAndApiKey,
        @Body HashMap<String, Object> body
    );
}
