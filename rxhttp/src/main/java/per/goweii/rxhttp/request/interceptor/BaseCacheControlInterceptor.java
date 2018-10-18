package per.goweii.rxhttp.request.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.request.Api;
import per.goweii.rxhttp.request.utils.NonNullUtils;

/**
 * 描述：缓存过滤器
 * 在基类过滤掉非GET请求和未配置{@link Api.Header#CACHE_ALIVE_SECOND}的请求
 *
 * @author Cuizhen
 * @date 2018/10/18
 */
public class BaseCacheControlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!TextUtils.equals(request.method(), "GET")) {
            return chain.proceed(request);
        }
        List<String> headers = request.headers(Api.Header.CACHE_ALIVE_SECOND);
        if (!NonNullUtils.check(headers)) {
            return chain.proceed(request);
        }
        int age = getCacheControlAge(headers.get(0));
        Request requestCached = getCacheRequest(request, age);
        Response response =  chain.proceed(requestCached);
        return getCacheResponse(response, age);
    }

    @NonNull
    protected Request getCacheRequest(Request request, int age){
        return request;
    }

    @NonNull
    protected Response getCacheResponse(Response response, int age){
        return response;
    }

    private int getCacheControlAge(String age) {
        try {
            return Integer.parseInt(age);
        } catch (NumberFormatException ignore) {
            return 0;
        }
    }
}