package per.goweii.rxhttp.download.setting;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
        return 5000;
    }

    @Nullable
    @Override
    public String getSaveDirName() {
        return null;
    }
}
