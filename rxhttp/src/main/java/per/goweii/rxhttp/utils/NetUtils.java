package per.goweii.rxhttp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import per.goweii.rxhttp.RxHttp;

/**
 * 描述：判断网络的辅助类
 *
 * @author Cuizhen
 * @date 2018/7/20-下午2:21
 */
public class NetUtils {

    /**
     * 判断是否有网络
     *
     * @return 返回值
     */
    public static boolean isConnected() {
        if (RxHttp.getAppContext() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) RxHttp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    return networkInfo.isAvailable();
                }
            }
        }
        return false;
    }
}
