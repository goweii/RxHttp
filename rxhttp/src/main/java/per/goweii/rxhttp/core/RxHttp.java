package per.goweii.rxhttp.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import per.goweii.rxhttp.core.exception.RxHttpUninitializedException;
import per.goweii.rxhttp.download.exception.NullDownloadSettingException;
import per.goweii.rxhttp.download.setting.DownloadSetting;
import per.goweii.rxhttp.request.exception.NullRequestSettingException;
import per.goweii.rxhttp.request.setting.RequestSetting;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
@SuppressLint("StaticFieldLeak")
public class RxHttp {

    public static final String BASE_URL_REDIRECT = "Base-Url-Redirect";

    private static RxHttp INSTANCE = null;

    private final Context mAppContext;
    private RequestSetting mRequestSetting = null;
    private DownloadSetting mDownloadSetting = null;

    private RxHttp(Context context) {
        mAppContext = context;
    }

    public static void init(@NonNull Context context) {
        INSTANCE = new RxHttp(context.getApplicationContext());
    }

    public static RxHttp getInstance() {
        if (INSTANCE == null) {
            throw new RxHttpUninitializedException();
        }
        return INSTANCE;
    }

    public static void initRequest(@NonNull RequestSetting setting) {
        getInstance().mRequestSetting = setting;
    }

    public static void initDownload(@NonNull DownloadSetting setting) {
        getInstance().mDownloadSetting = setting;
    }

    public static Context getAppContext() {
        return getInstance().mAppContext;
    }

    @NonNull
    public static RequestSetting getRequestSetting() {
        RequestSetting setting = getInstance().mRequestSetting;
        if (setting == null) {
            throw new NullRequestSettingException();
        }
        return setting;
    }

    @NonNull
    public static DownloadSetting getDownloadSetting() {
        DownloadSetting setting = getInstance().mDownloadSetting;
        if (setting == null) {
            throw new NullDownloadSettingException();
        }
        return setting;
    }
}
