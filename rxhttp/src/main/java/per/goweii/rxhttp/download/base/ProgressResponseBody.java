package per.goweii.rxhttp.download.base;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import per.goweii.rxhttp.download.interceptor.ProgressInterceptor;
import per.goweii.rxhttp.download.listener.ProgressListener;

/**
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private ProgressListener listener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener listener) {
        this.responseBody = responseBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            private long totalReadBytes = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long readBytes = super.read(sink, byteCount);
                totalReadBytes += readBytes == -1 ? 0 : readBytes;
                if (null != listener) {
                    listener.onUpdate(totalReadBytes, contentLength(), readBytes == -1);
                }
                return readBytes;
            }
        };
    }
}
