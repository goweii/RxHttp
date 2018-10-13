package per.goweii.rxhttp.exception;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public class RxHttpUninitializedException extends RuntimeException {
    public RxHttpUninitializedException() {
        super("RxHttp has not been initialized in Application yet.");
    }
}
