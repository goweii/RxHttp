package per.goweii.rxhttp.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.RxHttp;

/**
 * 描述：添加公共请求参数
 *
 * @author Cuizhen
 * @date 2018/9/28
 */
public class PublicQueryParameterInterceptor implements Interceptor {

    public static void addTo(@NonNull OkHttpClient.Builder builder) {
        Map<String, String> parameters = RxHttp.getSetting().getPublicQueryParameter();
        if (parameters != null && !parameters.isEmpty()) {
            builder.addInterceptor(new PublicQueryParameterInterceptor());
        }
    }

    private PublicQueryParameterInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Map<String, String> parameters = RxHttp.getSetting().getPublicQueryParameter();
        if (parameters == null || parameters.isEmpty()) {
            return chain.proceed(original);
        }
        HttpUrl.Builder builder = original.url().newBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        Request request = original.newBuilder()
                .method(original.method(), original.body())
                .url(builder.build())
                .build();
        return chain.proceed(request);
    }
}