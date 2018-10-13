package per.goweii.rxhttp.interceptor;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.utils.BaseUrlUtils;

/**
 * 描述：多BaseUrl
 *
 * @author Cuizhen
 * @date 2018/9/28
 */
public class MultiBaseUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Map<String, String> urls = RxHttp.getSetting().getMultiBaseUrl();
        if (urls == null || urls.isEmpty()) {
            return chain.proceed(original);
        }
        List<String> urlNames = original.headers(RxHttp.MULTI_BASE_URL_NAME);
        if (urlNames == null || urlNames.isEmpty()) {
            return chain.proceed(original);
        }
        Request.Builder builder = original.newBuilder();
        builder.removeHeader(RxHttp.MULTI_BASE_URL_NAME);
        String urlName = urlNames.get(0);
        String url = urls.get(urlName);
        if (url == null) {
            return chain.proceed(original);
        }
        HttpUrl baseUrl = HttpUrl.parse(BaseUrlUtils.checkBaseUrl(url));
        if (baseUrl == null) {
            return chain.proceed(original);
        }
        HttpUrl newHttpUrl = original.url().newBuilder()
                .scheme(baseUrl.scheme())
                .host(baseUrl.host())
                .port(baseUrl.port())
                .build();
        Request newRequest = builder.url(newHttpUrl).build();
        return chain.proceed(newRequest);
    }
}