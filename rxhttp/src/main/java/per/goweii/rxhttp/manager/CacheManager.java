package per.goweii.rxhttp.manager;

import java.io.File;

import okhttp3.Cache;
import per.goweii.rxhttp.RxHttp;
import per.goweii.rxhttp.utils.CacheUtils;

/**
 * 描述：缓存帮助类
 *
 * @author Cuizhen
 * @date 2018/9/4
 */
public class CacheManager {

    private static CacheManager INSTANCE = null;
    private File cacheFile;
    private Cache mCache;

    private CacheManager() {
        cacheFile = new File(CacheUtils.getCacheDir(), RxHttp.getSetting().getCacheDirName());
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
    }

    static CacheManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CacheManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CacheManager();
                }
            }
        }
        return INSTANCE;
    }

    Cache getCache() {
        if (mCache == null) {
            mCache = new Cache(cacheFile, RxHttp.getSetting().getCacheSize());
        }
        return mCache;
    }
}
