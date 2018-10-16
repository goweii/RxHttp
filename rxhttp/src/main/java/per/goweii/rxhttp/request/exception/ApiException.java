package per.goweii.rxhttp.request.exception;

/**
 * 描述：服务器请求成功，返回失败码时抛出，方便统一处理
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public class ApiException extends Exception {

    private final int code;
    private final String msg;

    public ApiException(int code, String msg) {
        super(msg + "(code=" + code + ")");
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
