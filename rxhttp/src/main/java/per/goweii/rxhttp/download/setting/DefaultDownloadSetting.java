package per.goweii.rxhttp.download.setting;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import per.goweii.rxhttp.download.DownloadInfo;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/16
 */
public class DefaultDownloadSetting implements DownloadSetting {

    @NonNull
    @Override
    public String getBaseUrl() {
        return "http://api.rxhttp.download/";
    }

    @Override
    public long getTimeout() {
        return 60000;
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

    @Nullable
    @Override
    public String getSaveDirPath() {
        return null;
    }

    @NonNull
    @Override
    public DownloadInfo.Mode getDefaultDownloadMode() {
        return DownloadInfo.Mode.APPEND;
    }
}
