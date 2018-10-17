package per.goweii.rxhttp.request;

import com.google.gson.Gson;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import per.goweii.rxhttp.BuildConfig;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.manager.BaseClientManager;
import per.goweii.rxhttp.core.utils.BaseUrlUtils;
import per.goweii.rxhttp.core.utils.SDCardUtils;
import per.goweii.rxhttp.request.interceptor.BaseUrlRedirectInterceptor;
import per.goweii.rxhttp.request.interceptor.CacheControlInterceptor;
import per.goweii.rxhttp.request.interceptor.PublicQueryParameterInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
class RequestClientManager extends BaseClientManager {

    private static RequestClientManager INSTANCE = null;
    private final Retrofit mRetrofit;

    private RequestClientManager() {
        mRetrofit = create();
    }

    private static RequestClientManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RequestClientManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RequestClientManager();
                }
            }
        }
        return INSTANCE;
    }

    static <T> T getService(Class<T> clazz) {
        return getInstance().mRetrofit.create(clazz);
    }

    @Override
    protected Retrofit create(){
        return new Retrofit.Builder()
                .client(createOkHttpClient())
                .baseUrl(BaseUrlUtils.checkBaseUrl(RxHttp.getRequestSetting().getBaseUrl()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    private OkHttpClient createOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        builder.cache(createCache())
                .connectTimeout(RxHttp.getRequestSetting().getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(RxHttp.getRequestSetting().getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(RxHttp.getRequestSetting().getTimeout(), TimeUnit.MILLISECONDS);
        BaseUrlRedirectInterceptor.addTo(builder);
        PublicQueryParameterInterceptor.addTo(builder);
        CacheControlInterceptor.addTo(builder);
        Interceptor[] interceptors = RxHttp.getRequestSetting().getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        Interceptor[] networkInterceptors = RxHttp.getRequestSetting().getNetworkInterceptors();
        if (networkInterceptors != null && networkInterceptors.length > 0) {
            for (Interceptor interceptor : networkInterceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        return builder.build();
    }

    private Cache createCache() {
        File cacheFile = new File(SDCardUtils.getCacheDir(), RxHttp.getRequestSetting().getCacheDirName());
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return new Cache(cacheFile, RxHttp.getRequestSetting().getCacheSize());
    }
}
