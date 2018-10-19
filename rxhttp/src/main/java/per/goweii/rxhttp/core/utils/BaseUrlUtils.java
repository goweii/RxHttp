package per.goweii.rxhttp.core.utils;

/**
 * 描述：检查BaseUrl是否以"/"结尾
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
