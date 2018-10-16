package per.goweii.rxhttp.request.exception;

/**
 * @author Cuizhen
 * @date 2018/10/15
 */
public class NullRequestSettingException extends RuntimeException {
    public NullRequestSettingException() {
        super("RequestSetting未设置");
    }
}
