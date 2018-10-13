package per.goweii.rxhttp.manager;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import per.goweii.rxhttp.BuildConfig;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.interceptor.MultiBaseUrlInterceptor;
import per.goweii.rxhttp.interceptor.PublicParamsInterceptor;

/**
 * 描述：OkHttp帮助类
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
            mClient = builder.cache(mCache)
                    .connectTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                    .addInterceptor(new MultiBaseUrlInterceptor())
                    .addInterceptor(new PublicParamsInterceptor())
                    .build();
        }
        return mClient;
    }
}
