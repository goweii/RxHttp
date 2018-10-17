package per.goweii.rxhttp.request.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.request.Api;
import per.goweii.rxhttp.request.utils.NetUtils;
import per.goweii.rxhttp.request.utils.NonNullUtils;

public class CacheControlInterceptor implements Interceptor {

    public static void addTo(@NonNull OkHttpClient.Builder builder) {
        builder.addInterceptor(new CacheControlInterceptor());
    }

    private CacheControlInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!TextUtils.equals(request.method(), "GET")) {
            return chain.proceed(request);
        }
        List<String> headers = request.headers(Api.Header.CACHE_CONTROL_AGE);
        if (!NonNullUtils.check(headers)) {
            return chain.proceed(request);
        }
        request = request.newBuilder()
                .removeHeader(Api.Header.CACHE_CONTROL_AGE)
                .cacheControl(getCacheControl(getCacheControlAge(headers.get(0))))
                .build();
        return chain.proceed(request);
    }

    private CacheControl getCacheControl(int age) {
        if (!NetUtils.isConnected()) {
            return CacheControl.FORCE_CACHE;
        } else {
            if (age <= 0) {
                return CacheControl.FORCE_NETWORK;
            } else {
                return new CacheControl.Builder().maxAge(age, TimeUnit.SECONDS).build();
            }
        }
    }

    private int getCacheControlAge(String age) {
        try {
            return Integer.parseInt(age);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }
}