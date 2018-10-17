package per.goweii.rxhttp.request.exception;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLException;

import per.goweii.rxhttp.request.setting.RequestSetting;
import per.goweii.rxhttp.request.utils.NetUtils;
import retrofit2.HttpException;

/**
 * 集中处理请求中异常，可通过继承自定义，在
 * {@link RequestSetting#getExceptionHandle()}中返回
 *
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ExceptionHandle {

    private Throwable e;
    private int code;
    private String msg;

    public final void handle(Throwable e) {
        this.e = e;
        this.code = onGetCode(e);
        this.msg = onGetMsg(code);
    }

    /**
     * 重写该方法去返回异常对应的错误码
     *
     * @param e Throwable
     * @return 错误码
     */
    protected int onGetCode(Throwable e) {
        if (!NetUtils.isConnected()) {
            return Code.NET;
        } else {
            if (e instanceof SocketTimeoutException) {
                return Code.TIMEOUT;
            } else if (e instanceof HttpException) {
                return Code.HTTP;
            } else if (e instanceof UnknownHostException || e instanceof ConnectException) {
                return Code.HOST;
            } else if (e instanceof JsonParseException || e instanceof ParseException || e instanceof JSONException) {
                return Code.JSON;
            } else if (e instanceof SSLException) {
                return Code.SSL;
            } else {
                return Code.UNKNOWN;
            }
        }
    }

    /**
     * 重写该方法去返回错误码对应的错误信息
     *
     * @param code 错误码
     * @return 错误信息
     */
    protected String onGetMsg(int code) {
        String msg;
        switch (code) {
            default:
                msg = "未知错误，请稍后重试";
                break;
            case Code.NET:
                msg = "网络连接失败，请检查网络设置";
                break;
            case Code.TIMEOUT:
                msg = "网络状况不稳定，请稍后重试";
                break;
            case Code.JSON:
                msg = "JSON解析异常";
                break;
            case Code.HTTP:
                msg = "请求错误，请稍后重试";
                break;
            case Code.HOST:
                msg = "服务器连接失败，请检查网络设置";
                break;
            case Code.SSL:
                msg = "证书验证失败";
                break;
        }
        return msg;
    }

    public final int getCode() {
        return code;
    }

    public final String getMsg() {
        return msg;
    }

    public final Throwable getException() {
        return e;
    }

    public interface Code {
        int UNKNOWN = -1;
        int NET = 0;
        int TIMEOUT = 1;
        int JSON = 2;
        int HTTP = 3;
        int HOST = 4;
        int SSL = 5;
    }
}
