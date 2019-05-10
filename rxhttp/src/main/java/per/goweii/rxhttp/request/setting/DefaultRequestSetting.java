package per.goweii.rxhttp.request.setting;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * 描述：网络请求设置（默认）
 *
 * @author Cuizhen
 * @date 2018/7/20
 */
public abstract class DefaultRequestSetting implements RequestSetting {

    @Nullable
    @Override
    public Map<String, String> getRedirectBaseUrl() {
        return null;
    }

    @Nullable
    @Override
    public Map<Class<?>, String> getServiceBaseUrl() {
        return null;
    }

    @Override
    public int[] getMultiSuccessCode() {
        return null;
    }

    @Override
    public long getTimeout() {
        return 5000;
    }

    @Override
    public long getConnectTimeout() {
        return 0;
    }

    @Override
    public long getReadTimeout() {
        return 0;
    }

    @Override
    public long getWriteTimeout() {
        return 0;
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

    @Nullable
    @Override
    public Map<String, String> getStaticPublicQueryParameter() {
        return null;
    }

    @Nullable
    @Override
    public Map<String, ParameterGetter> getDynamicPublicQueryParameter() {
        return null;
    }

    @Nullable
    @Override
    public Map<String, String> getStaticHeaderParameter() {
        return null;
    }

    @Nullable
    @Override
    public Map<String, ParameterGetter> getDynamicHeaderParameter() {
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

    @Override
    public boolean ignoreSslForHttps() {
        return false;
    }

    @Override
    public boolean enableTls12BelowAndroidKitkat() {
        return true;
    }

    @Override
    public void setOkHttpClient(OkHttpClient.Builder builder) {
    }

    @Nullable
    @Override
    public Gson getGson() {
        return null;
    }

    @Override
    public boolean isDebug() {
        return false;
    }
}
