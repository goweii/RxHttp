package per.goweii.rxhttp.request.setting;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import per.goweii.rxhttp.request.exception.ExceptionHandle;

/**
 * 描述：网络请求设置
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public interface RequestSetting {

    /**
     * 设置默认BaseUrl
     */
    @NonNull
    String getBaseUrl();

    /**
     * 用于对不同的请求设置不同的BaseUrl
     * 需要配合Retrofit的@Headers注解使用
     * 如：@Headers({RxHttp.BASE_URL_REDIRECT + ":" + 别名})
     *
     * @return Map 别名,BaseUrl
     */
    @Nullable
    Map<String, String> getRedirectBaseUrl();

    /**
     * 用于对一组接口设置BaseUrl
     * 这种设置方法对资源占用较大，实现方式为每组的请求创建不同的Retrofit和OkHttpClient实例，设置均相同，及下面的设置
     * 建议在少数请求需要单独设置BaseUrl时使用{@link #getRedirectBaseUrl()}
     *
     * @return Map 接口类,BaseUrl
     */
    @Nullable
    Map<Class<?>, String> getServiceBaseUrl();

    int getSuccessCode();

    @Nullable
    int[] getMultiSuccessCode();

    /**
     * 获取默认超时时长，单位为毫秒数
     */
    @IntRange(from = 1)
    long getTimeout();

    /**
     * 获取Connect超时时长，单位为毫秒数
     * 返回0则取getTimeout
     */
    @IntRange(from = 0)
    long getConnectTimeout();

    /**
     * 获取Read超时时长，单位为毫秒数
     * 返回0则取getTimeout
     */
    @IntRange(from = 0)
    long getReadTimeout();

    /**
     * 获取Write超时时长，单位为毫秒数
     * 返回0则取getTimeout
     */
    @IntRange(from = 0)
    long getWriteTimeout();

    /**
     * 获取网络缓存的文件夹名
     */
    @NonNull
    String getCacheDirName();

    /**
     * 获取网络缓存的最大值
     */
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

    void setOkHttpClient(OkHttpClient.Builder builder);

    boolean isDebug();

}
