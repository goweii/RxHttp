package per.goweii.rxhttp.core.exception;

import per.goweii.rxhttp.core.RxHttp;

/**
 * 描述：在调用网络请求之前应该先进行初始化，建议在Application中初始化
 * {@link RxHttp#init(android.content.Context)}
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public class RxHttpUninitializedException extends RuntimeException {
    public RxHttpUninitializedException() {
        super("RxHttp未初始化");
    }
}
