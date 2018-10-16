package per.goweii.rxhttp.request.setting;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import okhttp3.Interceptor;
import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * 描述：网络请求设置
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public interface RequestSetting {

    @NonNull
    String getBaseUrl();

    /**
     * 用于对不同的请求设置不同的BaseUrl
     * 需要配合Retrofit的@Headers注解使用
     * 如：@Headers({RxHttp.BASE_URL_REDIRECT + ":" + 别名})
     * @return Map<别名, url>
     */
    @Nullable
    Map<String, String> getMultiBaseUrl();

    int getSuccessCode();

    @Nullable
    int[] getMultiSuccessCode();

    @IntRange(from = 1)
    long getTimeout();

    @NonNull
    String getCacheDirName();

    @IntRange(from = 1)
    long getCacheSize();

    @Nullable
    Map<String, String> getStaticPublicQueryParameter();

    @Nullable
    Map<String, ParameterGetter> getDynamicPublicQueryParameter();

    @Nullable
    <E extends ExceptionHandle> E getExceptionHandle();

    @Nullable
    Interceptor[] getInterceptors();

    @Nullable
    Interceptor[] getNetworkInterceptors();

}
