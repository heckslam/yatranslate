package ru.devtron.yatranslate.data.network.interceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor для сеттинга кэширования
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        //rewrite response header to force use of cache
        CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(2, TimeUnit.DAYS)
                .build();

        return response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build();
    }
}
