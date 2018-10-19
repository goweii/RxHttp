package per.goweii.rxhttp.download.setting;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import per.goweii.rxhttp.download.DownloadInfo;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
public interface DownloadSetting {

    @NonNull
    String getBaseUrl();

    /**
     * 获取默认超时时长，单位为毫秒数
     */
    @IntRange(from = 1)
    long getTimeout();

    /**
     * 获取Connect超时时长，单位为毫秒数
     * 返回0则去getTimeout
     */
    @IntRange(from = 0)
    long getConnectTimeout();

    /**
     * 获取Read超时时长，单位为毫秒数
     * 返回0则去getTimeout
     */
    @IntRange(from = 0)
    long getReadTimeout();

    /**
     * 获取Write超时时长，单位为毫秒数
     * 返回0则去getTimeout
     */
    @IntRange(from = 0)
    long getWriteTimeout();

    @Nullable
    String getSaveDirPath();

    @NonNull
    DownloadInfo.Mode getDefaultDownloadMode();
}
