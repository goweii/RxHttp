package per.goweii.rxhttp.request;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.manager.BaseClientManager;
import per.goweii.rxhttp.core.utils.BaseUrlUtils;
import per.goweii.rxhttp.core.utils.SDCardUtils;
import per.goweii.rxhttp.request.interceptor.BaseUrlRedirectInterceptor;
import per.goweii.rxhttp.request.interceptor.CacheControlInterceptor;
import per.goweii.rxhttp.request.interceptor.CacheControlNetworkInterceptor;
import per.goweii.rxhttp.request.interceptor.PublicHeadersInterceptor;
import per.goweii.rxhttp.request.interceptor.PublicQueryParameterInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 描述：构建Retrofit实例，采用单例模式，全局共享同一个Retrofit
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
class RequestClientManager extends BaseClientManager {

    private static RequestClientManager INSTANCE = null;
    private final Retrofit mRetrofit;
    private Map<Class<?>, Retrofit> mRetrofitMap = null;

    private RequestClientManager() {
        mRetrofit = create();
    }

    /**
     * 采用单例模式
     *
     * @return RequestClientManager
     */
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

    /**
     * 创建Api接口实例
     *
     * @param clazz Api接口类
     * @param <T>   Api接口
     * @return Api接口实例
     */
    static <T> T getService(Class<T> clazz) {
        return getInstance().getRetrofit(clazz).create(clazz);
    }

    private Retrofit getRetrofit(Class<?> clazz) {
        if (clazz == null) {
            return mRetrofit;
        }
        Retrofit retrofit = null;
        if (mRetrofitMap != null && mRetrofitMap.size() > 0) {
            Iterator<Map.Entry<Class<?>, Retrofit>> iterator = mRetrofitMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Class<?>, Retrofit> entry = iterator.next();
                if (TextUtils.equals(entry.getKey().getName(), clazz.getName())) {
                    retrofit = entry.getValue();
                    if (retrofit == null) {
                        iterator.remove();
                    }
                    break;
                }
            }
        }
        if (retrofit != null) {
            return retrofit;
        }
        Map<Class<?>, String> baseUrlMap = RxHttp.getRequestSetting().getServiceBaseUrl();
        if (baseUrlMap == null || baseUrlMap.size() == 0) {
            return mRetrofit;
        }
        String baseUrl = null;
        for (Map.Entry<Class<?>, String> entry : baseUrlMap.entrySet()) {
            if (TextUtils.equals(entry.getKey().getName(), clazz.getName())) {
                baseUrl = entry.getValue();
                break;
            }
        }
        if (baseUrl == null) {
            return mRetrofit;
        }
        retrofit = create(baseUrl);
        mRetrofitMap.put(clazz, retrofit);
        return retrofit;
    }

    /**
     * 创建Retrofit实例
     */
    @Override
    protected Retrofit create() {
        return create(RxHttp.getRequestSetting().getBaseUrl());
    }

    /**
     * 创建Retrofit实例
     */
    private Retrofit create(String baseUrl) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .client(createOkHttpClient())
                .baseUrl(BaseUrlUtils.checkBaseUrl(baseUrl));
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        Gson gson = RxHttp.getRequestSetting().getGson();
        if (gson == null) {
            gson = new Gson();
        }
        builder.addConverterFactory(GsonConverterFactory.create(gson));
        return builder.build();
    }

    /**
     * 创建OkHttpClient实例
     *
     * @return OkHttpClient
     */
    private OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 设置调试模式打印日志
        if (RxHttp.getRequestSetting().isDebug()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        // 设置缓存
        builder.cache(createCache());
        // 设置3个超时时长
        long timeout = RxHttp.getRequestSetting().getTimeout();
        long connectTimeout = RxHttp.getRequestSetting().getConnectTimeout();
        long readTimeout = RxHttp.getRequestSetting().getReadTimeout();
        long writeTimeout = RxHttp.getRequestSetting().getWriteTimeout();
        builder.connectTimeout(connectTimeout > 0 ? connectTimeout : timeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(readTimeout > 0 ? readTimeout : timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(writeTimeout > 0 ? writeTimeout : timeout, TimeUnit.MILLISECONDS);
        // 设置应用层拦截器
        BaseUrlRedirectInterceptor.addTo(builder);
        PublicHeadersInterceptor.addTo(builder);
        PublicQueryParameterInterceptor.addTo(builder);
        CacheControlInterceptor.addTo(builder);
        Interceptor[] interceptors = RxHttp.getRequestSetting().getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        // 设置网络层拦截器
        CacheControlNetworkInterceptor.addTo(builder);
        Interceptor[] networkInterceptors = RxHttp.getRequestSetting().getNetworkInterceptors();
        if (networkInterceptors != null && networkInterceptors.length > 0) {
            for (Interceptor interceptor : networkInterceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        RxHttp.getRequestSetting().setOkHttpClient(builder);
        return builder.build();
    }

    /**
     * 创建缓存
     *
     * @return Cache
     */
    private Cache createCache() {
        File cacheFile = new File(SDCardUtils.getCacheDir(), RxHttp.getRequestSetting().getCacheDirName());
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return new Cache(cacheFile, RxHttp.getRequestSetting().getCacheSize());
    }
}
