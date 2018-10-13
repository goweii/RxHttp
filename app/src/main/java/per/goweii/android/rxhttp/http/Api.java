package per.goweii.android.rxhttp.http;

import io.reactivex.Observable;
import per.goweii.android.rxhttp.bean.RecommendPoetryBean;
import per.goweii.android.rxhttp.bean.SinglePoetryBean;
import per.goweii.android.rxhttp.bean.WeatherBean;
import per.goweii.rxhttp.RxHttp;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/13
 */
public interface Api {

    /**
     * 随机单句诗词推荐
     */
    @GET("singlePoetry")
    Observable<ResponseBean<SinglePoetryBean>> singlePoetry();

    /**
     * 随机一首诗词推荐
     */
    @GET("recommendPoetry")
    Observable<ResponseBean<RecommendPoetryBean>> recommendPoetry();

    /**
     * 获取天气
     */
    @Headers({RxHttp.MULTI_BASE_URL_NAME + ":" + Config.BASE_URL_OTHER_NAME})
    @GET("weatherApi?")
    Observable<ResponseBean<WeatherBean>> weather(@Query("city") String city);
}
