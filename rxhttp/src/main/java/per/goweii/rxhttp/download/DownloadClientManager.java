package per.goweii.rxhttp.download;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.manager.BaseClientManager;
import per.goweii.rxhttp.core.utils.BaseUrlUtils;
import per.goweii.rxhttp.download.interceptor.RealNameInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
class DownloadClientManager extends BaseClientManager {

    private static DownloadClientManager INSTANCE = null;
    private final Retrofit mRetrofit;

    private DownloadClientManager() {
        mRetrofit = create();
    }

    /**
     * 采用单例模式
     *
     * @return RequestClientManager
     */
    private static DownloadClientManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadClientManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadClientManager();
                }
            }
        }
        return INSTANCE;
    }

    static DownloadApi getService() {
        return getInstance().mRetrofit.create(DownloadApi.class);
    }

    @Override
    protected Retrofit create() {
        return new Retrofit.Builder()
                .client(createOkHttpClient())
                .baseUrl(BaseUrlUtils.checkBaseUrl(RxHttp.getDownloadSetting().getBaseUrl()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient createOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        long timeout = RxHttp.getDownloadSetting().getTimeout();
        long connectTimeout = RxHttp.getDownloadSetting().getConnectTimeout();
        long readTimeout = RxHttp.getDownloadSetting().getReadTimeout();
        long writeTimeout = RxHttp.getDownloadSetting().getWriteTimeout();
        builder.connectTimeout(connectTimeout > 0 ? connectTimeout : timeout, TimeUnit.MILLISECONDS);
        builder.readTimeout(readTimeout > 0 ? readTimeout : timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(writeTimeout > 0 ? writeTimeout : timeout, TimeUnit.MILLISECONDS);
        RealNameInterceptor.addTo(builder);
        return builder.build();
    }
}
