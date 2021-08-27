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

    @NonNull
    public static RequestBodyUtils.Builder builder() {
        return new RequestBodyUtils.Builder();
    }

    @NonNull
    public static <T> Map<String, RequestBody> create(@NonNull String key, @NonNull T value) {
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
        @NonNull
        public <T> Builder add(@NonNull String key, @NonNull T value) {
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
        @NonNull
        public Builder addString(@NonNull String key, @NonNull String value) {
            RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
            mParams.put(key, body);
            return this;
        }

        /**
         * 添加参数File
         */
        @NonNull
        public Builder addFile(@NonNull String key, @NonNull File value) {
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
        @NonNull
        public Builder addFile(@NonNull String key, @NonNull String filePath) {
            return addFile(key, new File(filePath));
        }

        @NonNull
        public Builder addFile(@NonNull String key, @NonNull Uri uri) {
            String path = uri.getPath();
            if (path == null || path.isEmpty()) {
                return this;
            }
            return addFile(key, path);
        }

        /**
         * 构建RequestBody
         */
        @NonNull
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
