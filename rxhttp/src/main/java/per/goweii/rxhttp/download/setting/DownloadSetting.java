package per.goweii.rxhttp.download.setting;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
public interface DownloadSetting {

    @NonNull
    String getBaseUrl();

    @IntRange(from = 1)
    long getTimeout();

    @Nullable
    String getSaveDirName();
}
