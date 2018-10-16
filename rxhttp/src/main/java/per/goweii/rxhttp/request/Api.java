package per.goweii.rxhttp.request;

/**
 * 子类继承，用于创建一个API接口实例
 * 新写一个无参静态方法调用{@link #api(Class)}去创建一个接口实例
 * 方法{@link #api(Class)}出入的参数为ServiceInterface，建议为内部类
 *
 * @author Cuizhen
 * @date 2018/10/16
 */
public class Api {

    protected static <T> T api(Class<T> clazz) {
        return RequestClientManager.getService(clazz);
    }

}
