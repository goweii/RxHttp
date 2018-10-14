package per.goweii.rxhttp.exception;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLException;

import retrofit2.HttpException;

/**
 * 集中处理请求中异常
 *
 * @author CuiZhen
 * @date 2018/10/14
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ExceptionHandle {

    private int code;
    private String msg;
    private Throwable e;

    public void handle(Throwable e){
        if (e instanceof NetConnectException) {
            set(Code.NO_NET, "网络连接失败，请检查网络设置", e);
        } else if (e instanceof SocketTimeoutException) {
            set(Code.TIMEOUT, "请求超时", e);
        } else if (e instanceof HttpException) {
            set(Code.API, "请求错误", e);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {
            set(Code.UNKNOWN, "服务器连接失败，请检查网络设置", e);
        } else if (e instanceof JsonParseException
                || e instanceof ParseException
                || e instanceof JSONException) {
            set(Code.JSON_PARSE, "JSON解析异常", e);
        } else if (e instanceof SSLException) {
            set(Code.SSL, "证书验证失败", e);
        } else {
            set(Code.UNKNOWN, "未知错误，请稍后重试", e);
        }
    }

    protected void set(int code, String msg, Throwable e){
        this.e = e;
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Throwable getException() {
        return e;
    }

    public interface Code{
        int UNKNOWN = -1;
        int NO_NET = 0;
        int TIMEOUT = 1;
        int JSON_PARSE = 2;
        int API = 3;
        int SSL = 4;
    }
}
