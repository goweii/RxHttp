package per.goweii.rxhttp.exception;

/**
 * 描述：在调用网络请求之前应该先进行初始化，建议在Application中初始化
 * {@link per.goweii.rxhttp.RxHttp#init(android.content.Context, per.goweii.rxhttp.setting.HttpSetting)}
 *
 * @author Cuizhen
 * @date 2018/10/12
 */
public class RxHttpUninitializedException extends RuntimeException {
    public RxHttpUninitializedException() {
        super("RxHttp has not been initialized in Application yet.");
    }
}
