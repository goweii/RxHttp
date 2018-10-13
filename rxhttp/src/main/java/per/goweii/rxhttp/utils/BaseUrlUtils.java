package per.goweii.rxhttp.utils;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/13
 */
public class BaseUrlUtils {

    public static String checkBaseUrl(String url) {
        if (url.endsWith("/")) {
            return url;
        } else {
            return url + "/";
        }
    }
}
