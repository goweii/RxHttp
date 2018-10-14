package per.goweii.rxhttp.manager;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import per.goweii.rxhttp.BuildConfig;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.interceptor.BaseUrlRedirectInterceptor;
import per.goweii.rxhttp.interceptor.PublicQueryParameterInterceptor;

/**
 * OkHttp
 *
 * @author Cuizhen
 * @date 2018/9/4
 */
class OkHttpManager {

    private static OkHttpManager INSTANCE = null;
    private final Cache mCache;
    private OkHttpClient mClient;

    private OkHttpManager() {
        mCache = CacheManager.getInstance().getCache();
    }

    static OkHttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (OkHttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new OkHttpManager();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient getClient() {
        if (mClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(logging);
            }
            builder.cache(mCache)
                    .connectTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS);
            addInterceptor(builder);
            addNetworkInterceptor(builder);
            mClient = builder.build();
        }
        return mClient;
    }

    private void addInterceptor(OkHttpClient.Builder builder){
        BaseUrlRedirectInterceptor.addTo(builder);
        PublicQueryParameterInterceptor.addTo(builder);
        Interceptor[] interceptors = RxHttp.getSetting().getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
    }

    private void addNetworkInterceptor(OkHttpClient.Builder builder){
        Interceptor[] interceptors = RxHttp.getSetting().getNetworkInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
    }
}
