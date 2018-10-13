package per.goweii.rxhttp.utils;

import android.os.Environment;

import java.io.File;

import per.goweii.rxhttp.RxHttp;

/**
 * 清除缓存辅助类
 * @author Cuizhen
 * @date 18/4/23
 */
public class CacheUtils {

    public static String getCacheDir() {
        File cacheFile = null;
        if (isSDCardAlive()) {
            cacheFile = RxHttp.getAppContext().getExternalCacheDir();
        }
        if (cacheFile == null) {
            cacheFile = RxHttp.getAppContext().getCacheDir();
        }
        return cacheFile.getAbsolutePath();
    }

    private static boolean isSDCardAlive() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}