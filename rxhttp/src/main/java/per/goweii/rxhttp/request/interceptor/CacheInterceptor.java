package per.goweii.rxhttp.request.interceptor;

import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.request.utils.NetUtils;

public class CacheInterceptor implements Interceptor {
    /**
     * 强制使用网络请求
     */
    public static final CacheControl FORCE_NETWORK = new CacheControl.Builder().noCache().build();

    /**
     * 强制性使用本地缓存，如果本地缓存不满足条件，则会返回code为504
     */
    public static final CacheControl FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxAge(Integer.MAX_VALUE, TimeUnit.SECONDS).build();

    /**
     * 配置需要缓存的GET请求
     */
    private static final String[] CACHE_GET_API = new String[]{
            ""
    };

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        if (TextUtils.equals(request.method(), "GET") && isNeedCacheGet(url.toString())) {
            if (NetUtils.isConnected()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            } else {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
        }
        return chain.proceed(request);
    }

    private boolean isNeedCacheGet(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        for (String api : CACHE_GET_API) {
            String cacheUrl = RxHttp.getRequestSetting().getBaseUrl() + api;
            if (url.contains(cacheUrl)) {
                return true;
            }
        }
        return false;
    }
}