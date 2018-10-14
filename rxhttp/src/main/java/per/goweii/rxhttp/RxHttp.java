package per.goweii.rxhttp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import per.goweii.rxhttp.exception.RxHttpUninitializedException;
import per.goweii.rxhttp.setting.HttpSetting;
import per.goweii.rxhttp.manager.RetrofitManager;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
@SuppressLint("StaticFieldLeak")
public class RxHttp {

    public static final String BASE_URL_REDIRECT = "base_url_redirect";

    private static RxHttp INSTANCE = null;

    private final Context mAppContext;
    private final HttpSetting mSetting;

    private RxHttp(Context context, HttpSetting setting) {
        mAppContext = context;
        mSetting = setting;
    }

    public static void init(@NonNull Context context, @NonNull HttpSetting setting) {
        INSTANCE = new RxHttp(context.getApplicationContext(), setting);
    }

    public static RxHttp getInstance() {
        if (INSTANCE == null) {
            throw new RxHttpUninitializedException();
        }
        return INSTANCE;
    }

    public static Context getAppContext() {
        return getInstance().mAppContext;
    }

    public static HttpSetting getSetting() {
        return getInstance().mSetting;
    }

    public static <T> T getApi(Class<T> clazz) {
        return RetrofitManager.getService(clazz);
    }
}
