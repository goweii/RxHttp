package per.goweii.rxhttp.manager;

import com.google.gson.Gson;

import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.utils.BaseUrlUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit
 *
 * @author Cuizhen
 * @date 2018/9/4
 */
public class RetrofitManager {

    private static RetrofitManager INSTANCE = null;
    private final Retrofit mRetrofit;

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpManager.getInstance().getClient())
                .baseUrl(BaseUrlUtils.checkBaseUrl(RxHttp.getSetting().getBaseUrl()))
                .build();
    }

    public static <T> T getService(Class<T> clazz) {
        return RetrofitManager.getInstance().getRetrofit().create(clazz);
    }

    private static RetrofitManager getInstance() {
        if (INSTANCE == null) {
            synchronized (RetrofitManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RetrofitManager();
                }
            }
        }
        return INSTANCE;
    }

    private Retrofit getRetrofit() {
        return mRetrofit;
    }
}
