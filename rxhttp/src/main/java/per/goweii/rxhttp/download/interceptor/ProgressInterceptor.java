package per.goweii.rxhttp.download.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import per.goweii.rxhttp.download.base.ProgressResponseBody;
import per.goweii.rxhttp.download.listener.ProgressListener;

/**
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ProgressInterceptor implements Interceptor {

    private ProgressListener listener;

    public ProgressInterceptor(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response original = chain.proceed(chain.request());
        return original.newBuilder()
                .body(new ProgressResponseBody(original.body(), listener))
                .build();
    }
}
