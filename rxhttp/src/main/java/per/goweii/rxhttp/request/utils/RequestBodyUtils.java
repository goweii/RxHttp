package per.goweii.rxhttp.request.utils;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/9/4
 */
public class RequestBodyUtils {

    private RequestBodyUtils() {
    }

    public static RequestBodyUtils.Builder builder() {
        return new RequestBodyUtils.Builder();
    }

    public static <T> Map<String, RequestBody> create(String key, T value) {
        return builder().add(key, value).build();
    }

    public static class Builder {

        private final Map<String, RequestBody> mParams;

        private Builder() {
            mParams = new HashMap<>(1);
        }

        /**
         * 添加参数
         * 根据传进来的对象来判断是String还是File类型的参数
         */
        public <T> Builder add(String key, T value) {
            if (value instanceof String) {
                addString(key, (String) value);
            } else if (value instanceof File) {
                addFile(key, (File) value);
            }
            return this;
        }

        /**
         * 添加参数String
         */
        public Builder addString(@NonNull String key, String value) {
            if (value == null) {
                return this;
            }
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
            mParams.put(key, body);
            return this;
        }

        /**
         * 添加参数File
         */
        public Builder addFile(@NonNull String key, File value) {
            if (value == null) {
                return this;
            }
            if (!value.exists()) {
                return this;
            }
            if (value.isDirectory()) {
                return this;
            }
            mParams.put(getParamsKey(key, value), getParamsValue(value));
            return this;
        }

        /**
         * 添加参数File
         */
        public Builder addFile(String key, String filePath) {
            if (filePath == null) {
                return this;
            }
            return addFile(key, new File(filePath));
        }

        public Builder addFile(String key, Uri uri) {
            if (uri == null) {
                return this;
            }
            return addFile(key, uri.getPath());
        }

        /**
         * 构建RequestBody
         */
        public Map<String, RequestBody> build() {
            return mParams;
        }

        private String getParamsKey(@NonNull String key, @NonNull File file) {
            return key + "\"; filename=\"" + file.getName();
        }

        private RequestBody getParamsValue(@NonNull File file) {
            return RequestBody.create(MediaType.parse(FileUtils.getMimeType(file)), file);
        }
    }
}
