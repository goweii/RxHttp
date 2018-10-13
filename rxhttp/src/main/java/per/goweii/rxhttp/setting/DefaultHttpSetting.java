package per.goweii.rxhttp.setting;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * 描述：网络请求设置（默认）
 *
 * @author Cuizhen
 * @date 2018/7/20
 */
public abstract class DefaultHttpSetting implements HttpSetting {

    @Override
    public int[] getOtherSuccessCode() {
        return null;
    }

    @Override
    public int getErrorCode() {
        return 10000001;
    }

    @NonNull
    @Override
    public String getErrorMsg() {
        return "请求异常，请稍后重试";
    }

    @Override
    public long getTimeout() {
        return 5000;
    }

    @NonNull
    @Override
    public String getCacheDirName() {
        return "rxhttp_cache";
    }

    @Override
    public long getCacheSize() {
        return 10 * 1024 * 1024;
    }

    @Override
    public Map<String, String> getPublicQueryParameter() {
        return null;
    }
}
