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
