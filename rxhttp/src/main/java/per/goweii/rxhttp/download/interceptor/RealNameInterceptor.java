package per.goweii.rxhttp.download.interceptor;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class RealNameInterceptor implements Interceptor {

    private RealNameCallback mListener;

    public RealNameInterceptor(RealNameCallback listener) {
        this.mListener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (mListener != null){
            String realName = getHeaderFileName(response);
            if (!TextUtils.isEmpty(realName)) {
                mListener.onRealName(realName);
            }
        }
        return response;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment; filename=FileName.txt
     * Content-Disposition:attachment; filename*=UTF-8"%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    private String getHeaderFileName(Response response) {
        String realName = null;
        String dispositionHeader = response.header("Content-Disposition");
        if (!TextUtils.isEmpty(dispositionHeader)) {
            String[] strings = dispositionHeader.split(";");
            if (strings.length > 1) {
                realName = strings[1].replace(" ", "");
                realName = realName.replace("filename=", "");
                realName = realName.replace("filename*=", "");
                realName = realName.replace("UTF-8", "");
                realName = realName.replace("\"", "");
                realName = realName.replace("'", "");
            }
        }
        return realName;
    }

    public interface RealNameCallback{
        void onRealName(@NonNull String realName);
    }
}
