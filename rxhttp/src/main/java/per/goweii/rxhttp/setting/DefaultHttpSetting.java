package per.goweii.rxhttp.setting;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import okhttp3.Interceptor;
import per.goweii.rxhttp.exception.ExceptionHandle;

/**
 * 描述：网络请求设置（默认）
 *
 * @author Cuizhen
 * @date 2018/7/20
 */
public abstract class DefaultHttpSetting implements HttpSetting {

    @Nullable
    @Override
    public Map<String, String> getMultiBaseUrl() {
        return null;
    }

    @Override
    public int getSuccessCode() {
        return 200;
    }

    @Override
    public int[] getMultiSuccessCode() {
        return null;
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

    @Nullable
    @Override
    public <E extends ExceptionHandle> E getExceptionHandle() {
        return null;
    }

    @Nullable
    @Override
    public Interceptor[] getInterceptors() {
        return null;
    }

    @Nullable
    @Override
    public Interceptor[] getNetworkInterceptors() {
        return null;
    }
}
