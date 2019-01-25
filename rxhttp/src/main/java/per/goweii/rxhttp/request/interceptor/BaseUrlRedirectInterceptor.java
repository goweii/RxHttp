package per.goweii.rxhttp.request.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
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
        Map<String, String> urls = RxHttp.getRequestSetting().getRedirectBaseUrl();
        if (NonNullUtils.check(urls)) {
            builder.addInterceptor(new BaseUrlRedirectInterceptor());
        }
    }

    private BaseUrlRedirectInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Map<String, String> urls = RxHttp.getRequestSetting().getRedirectBaseUrl();
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
        String newUrl = urls.get(urlName);
        if (newUrl == null) {
            return chain.proceed(original);
        }
        HttpUrl newHttpUrl = HttpUrl.parse(BaseUrlUtils.checkBaseUrl(newUrl));
        if (newHttpUrl == null) {
            return chain.proceed(original);
        }
        HttpUrl oldHttpUrl = original.url();
        List<String> pathSegments = new ArrayList<>(oldHttpUrl.pathSegments());
        int oldCount = defaultBaseUrlPathSegmentCount();
        for (int i = 0; i < oldCount; i++) {
            pathSegments.remove(0);
        }
        HttpUrl.Builder newHttpUrlBuilder = oldHttpUrl.newBuilder()
                .scheme(newHttpUrl.scheme())
                .host(newHttpUrl.host())
                .port(newHttpUrl.port());
        int size1 = newHttpUrl.pathSegments().size();
        for (int i = size1 - 1; i >= 0; i--) {
            String segment = newHttpUrl.pathSegments().get(i);
            if (TextUtils.isEmpty(segment)){
                continue;
            }
            pathSegments.add(0, segment);
        }
        int size2 = oldHttpUrl.pathSegments().size();
        for (int i = 0; i < size2; i++) {
            newHttpUrlBuilder.removePathSegment(0);
        }
        for (int i = 0; i < pathSegments.size(); i++) {
            newHttpUrlBuilder.addPathSegment(pathSegments.get(i));
        }
        Request newRequest = builder.url(newHttpUrlBuilder.build()).build();
        return chain.proceed(newRequest);
    }

    private int defaultBaseUrlPathSegmentCount(){
        HttpUrl oldHttpUrl = HttpUrl.parse(BaseUrlUtils.checkBaseUrl(RxHttp.getRequestSetting().getBaseUrl()));
        if (oldHttpUrl == null) {
            return 0;
        }
        List<String> oldSegments = oldHttpUrl.pathSegments();
        if (oldSegments == null || oldSegments.size() == 0){
            return 0;
        }
        int count = oldSegments.size();
        if (TextUtils.isEmpty(oldSegments.get(count - 1))) {
            count--;
        }
        return count;
    }
}