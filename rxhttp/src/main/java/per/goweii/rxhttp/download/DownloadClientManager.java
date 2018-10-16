package per.goweii.rxhttp.download;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import per.goweii.rxhttp.core.RxHttp;
import per.goweii.rxhttp.core.manager.BaseClientManager;
import per.goweii.rxhttp.core.utils.BaseUrlUtils;
import per.goweii.rxhttp.download.base.DownloadApi;
import per.goweii.rxhttp.download.interceptor.ProgressInterceptor;
import per.goweii.rxhttp.download.interceptor.RangeInterceptor;
import per.goweii.rxhttp.download.interceptor.RealNameInterceptor;
import per.goweii.rxhttp.download.listener.ProgressListener;
import per.goweii.rxhttp.download.listener.RealNameListener;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
class DownloadClientManager extends BaseClientManager {

    private final ProgressListener mProgressListener;
    private final RealNameListener mRealNameListener;
    private final long mDownloadLength;
    private final long mContentLength;

    private DownloadClientManager(long downloadLength, long contentLength, ProgressListener progressListener, RealNameListener realNameListener) {
        mDownloadLength = downloadLength;
        mContentLength = contentLength;
        mProgressListener = progressListener;
        mRealNameListener = realNameListener;
    }

    static DownloadApi getService(long downloadLength, long contentLength, ProgressListener progressListener, RealNameListener realNameListener) {
        return new DownloadClientManager(downloadLength, contentLength, progressListener, realNameListener).create().create(DownloadApi.class);
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
        builder.connectTimeout(RxHttp.getDownloadSetting().getTimeout(), TimeUnit.MILLISECONDS);
        if (mProgressListener != null) {
            builder.addInterceptor(new ProgressInterceptor(mProgressListener));
        }
        if (mRealNameListener != null) {
            builder.addInterceptor(new RealNameInterceptor(mRealNameListener));
        }
        if (mDownloadLength > 0) {
            builder.addInterceptor(new RangeInterceptor(mDownloadLength, mContentLength));
        }
        return builder.build();
    }
}
