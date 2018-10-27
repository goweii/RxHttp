package per.goweii.rxhttp.request.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.utils.BaseUrlUtils;
import per.goweii.rxhttp.request.Api;
import per.goweii.rxhttp.request.utils.NonNullUtils;

/**
 * BaseUrl重定向
 *
 * @author Cuizhen
 * @date 2018/10/13
 */
public class BaseUrlRedirectInterceptor implements Interceptor {

    public static void addTo(@NonNull OkHttpClient.Builder builder) {
        Map<String, String> urls = RxHttp.getRequestSetting().getMultiBaseUrl();
        if (NonNullUtils.check(urls)) {
            builder.addInterceptor(new BaseUrlRedirectInterceptor());
        }
    }

    private BaseUrlRedirectInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Map<String, String> urls = RxHttp.getRequestSetting().getMultiBaseUrl();
        if (!NonNullUtils.check(urls)) {
            return chain.proceed(original);
        }
        List<String> urlNames = original.headers(Api.Header.BASE_URL_REDIRECT);
        if (!NonNullUtils.check(urlNames)) {
            return chain.proceed(original);
        }
        Request.Builder builder = original.newBuilder();
        builder.removeHeader(Api.Header.BASE_URL_REDIRECT);
        String urlName = urlNames.get(0);
        String url = urls.get(urlName);
        if (url == null) {
            return chain.proceed(original);
        }
        HttpUrl baseUrl = HttpUrl.parse(BaseUrlUtils.checkBaseUrl(url));
        if (baseUrl == null) {
            return chain.proceed(original);
        }
        HttpUrl.Builder newHttpUrlBuilder = original.url().newBuilder()
                .scheme(baseUrl.scheme())
                .host(baseUrl.host())
                .port(baseUrl.port());
        for (int i = 0; i < baseUrl.pathSegments().size(); i++) {
            String segment = baseUrl.pathSegments().get(i);
            if (TextUtils.isEmpty(segment)){
                break;
            }
            newHttpUrlBuilder.setPathSegment(i, segment);
        }
        HttpUrl newHttpUrl = newHttpUrlBuilder.build();
        Request newRequest = builder.url(newHttpUrl).build();
        return chain.proceed(newRequest);
    }
}