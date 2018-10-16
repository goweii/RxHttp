package per.goweii.rxhttp.request.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.request.setting.ParameterGetter;
import per.goweii.rxhttp.request.utils.NonNullUtils;

/**
 * 描述：添加公共请求参数
 *
 * @author Cuizhen
 * @date 2018/9/28
 */
public class PublicQueryParameterInterceptor implements Interceptor {

    public static void addTo(@NonNull OkHttpClient.Builder builder) {
        Map<String, String> staticParameters = RxHttp.getRequestSetting().getStaticPublicQueryParameter();
        Map<String, ParameterGetter> dynamicParameters = RxHttp.getRequestSetting().getDynamicPublicQueryParameter();
        if (NonNullUtils.check(staticParameters, dynamicParameters)) {
            builder.addInterceptor(new PublicQueryParameterInterceptor());
        }
    }

    private PublicQueryParameterInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        HttpUrl.Builder builder = original.url().newBuilder();

        Map<String, String> staticParameters = RxHttp.getRequestSetting().getStaticPublicQueryParameter();
        if (NonNullUtils.check(staticParameters)) {
            for (Map.Entry<String, String> entry : staticParameters.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Map<String, ParameterGetter> dynamicParameters = RxHttp.getRequestSetting().getDynamicPublicQueryParameter();
        if (NonNullUtils.check(dynamicParameters)) {
            for (Map.Entry<String, ParameterGetter> entry : dynamicParameters.entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue().get());
            }
        }

        Request request = original.newBuilder()
                .method(original.method(), original.body())
                .url(builder.build())
                .build();

        return chain.proceed(request);
    }
}