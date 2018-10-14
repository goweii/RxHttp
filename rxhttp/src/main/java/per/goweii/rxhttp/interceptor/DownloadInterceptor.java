package per.goweii.rxhttp.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import per.goweii.rxhttp.download.DownloadResponseBody;

/**
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class DownloadInterceptor implements Interceptor {

    private DownloadResponseBody.ProgressListener listener;

    public DownloadInterceptor(DownloadResponseBody.ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response original = chain.proceed(chain.request());
        return original.newBuilder()
                .body(new DownloadResponseBody(original.body(), listener))
                .build();
    }
}
