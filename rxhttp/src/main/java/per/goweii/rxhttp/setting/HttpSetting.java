package per.goweii.rxhttp.setting;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * 描述：网络请求设置
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public interface HttpSetting {

    @NonNull
    String getBaseUrl();

    /**
     * 用于对不同的请求设置不同的BaseUrl
     * 需要配合Retrofit的@Headers注解使用
     * 如：@Headers({RxHttp.MULTI_BASE_URL_NAME + ":" + 别名})
     * @return Map<别名, url>
     */
    @Nullable
    Map<String, String> getMultiBaseUrl();

    int getSuccessCode();

    @Nullable
    int[] getOtherSuccessCode();

    int getErrorCode();

    @NonNull
    String getErrorMsg();

    @IntRange(from = 1)
    long getTimeout();

    @NonNull
    String getCacheDirName();

    @IntRange(from = 1)
    long getCacheSize();

    @Nullable
    Map<String, String> getPublicQueryParameter();

}
