package per.goweii.rxhttp.manager;

import java.util.concurrent.TimeUnit;

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

    private OkHttpManager() {
    }

    static OkHttpClient createForRequest(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
        builder.cache(CacheManager.getInstance().getCache())
                .connectTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS);
        addInterceptor(builder);
        addNetworkInterceptor(builder);
        return builder.build();
    }

    static OkHttpClient createForDownload(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(RxHttp.getSetting().getTimeout(), TimeUnit.MILLISECONDS);
//        builder.addInterceptor(new DownloadInterceptor());
        return builder.build();
    }

    private static void addInterceptor(OkHttpClient.Builder builder){
        BaseUrlRedirectInterceptor.addTo(builder);
        PublicQueryParameterInterceptor.addTo(builder);
        Interceptor[] interceptors = RxHttp.getSetting().getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
    }

    private static void addNetworkInterceptor(OkHttpClient.Builder builder){
        Interceptor[] interceptors = RxHttp.getSetting().getNetworkInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
    }
}
