package per.goweii.rxhttp.download.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class RangeInterceptor implements Interceptor {

    private final long downloadLength;
    private final long contentLength;

    public RangeInterceptor(long downloadLength, long contentLength) {
        this.downloadLength = downloadLength;
        this.contentLength = contentLength;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request.newBuilder()
                .addHeader("RANGE", "bytes=" + downloadLength + "-" + contentLength)
                .build());
    }
}
