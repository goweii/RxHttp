package per.goweii.rxhttp.request.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.Locale;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
class FileUtils {

    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        if (fileName.endsWith(".")) {
            return "";
        }
        int index = fileName.lastIndexOf(".");
        if (index < 0) {
            return "";
        }
        return fileName.substring(index + 1).toLowerCase(Locale.US);
    }

    static String getMimeType(File file){
        String suffix = getSuffix(file);
        String mimeType = null;
        if (!TextUtils.isEmpty(suffix)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        }
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = "file/*";
        }
        return mimeType;
    }
}
