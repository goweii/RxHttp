package per.goweii.rxhttp.download.interceptor;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/21
 */
public class DownloadResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private BufferedSource source = null;
    private String realName = null;

    public DownloadResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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
        if (source == null) {
            source = Okio.buffer(source(responseBody.source()));
        }
        return source;
    }

    /**
     * 读取，回调进度接口
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                return super.read(sink, byteCount);
            }
        };
    }
}