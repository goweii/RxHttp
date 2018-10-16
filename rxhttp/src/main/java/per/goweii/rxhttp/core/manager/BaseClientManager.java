package per.goweii.rxhttp.core.manager;

import retrofit2.Retrofit;

/**
 * Retrofit
 *
 * @author Cuizhen
 * @date 2018/9/4
 */
public abstract class BaseClientManager {
    protected abstract Retrofit create();
}
