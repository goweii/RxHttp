package per.goweii.rxhttp.download.interceptor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * @author Cuizhen
 * @date 2018/10/18
 */
public class RealNameInterceptor implements Interceptor {

    public static void addTo(@NonNull OkHttpClient.Builder builder) {
        builder.addInterceptor(new RealNameInterceptor());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        String disposition = response.header("Content-Disposition");
        String realName = parseRealName(disposition);
        DownloadResponseBody responseBody = new DownloadResponseBody(response.body());
        responseBody.setRealName(realName);
        return response.newBuilder()
                .body(responseBody)
                .build();
    }

    private String parseRealName(@Nullable String disposition) {
        if (disposition == null || disposition.isEmpty()) {
            return null;
        }
        String[] parts = disposition.split(";");
        if (parts.length == 0) {
            return null;
        }
        Map<String, String> pairs = new HashMap<>(parts.length);
        for (String part : parts) {
            String s = part.trim();
            int i = s.indexOf("=");
            if (i == -1) {
                continue;
            }
            String k = s.substring(0, i);
            if (TextUtils.isEmpty(k)) {
                continue;
            }
            String v = s.substring(i + 1);
            if (TextUtils.isEmpty(v)) {
                continue;
            }
            pairs.put(k.trim(), v.trim());
        }
        if (pairs.isEmpty()) {
            return null;
        }
        String filename = pairs.get("filename*");
        if (filename == null || filename.isEmpty()) {
            filename = pairs.get("filename");
        }
        if (filename == null || filename.isEmpty()) {
            filename = pairs.get("name");
        }
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        if (filename.startsWith("\"") && filename.endsWith("\"")) {
            filename = filename.substring(1, filename.length() - 1);
        }
        filename = filename.replace("UTF-8", "");
        return filename;
    }
}