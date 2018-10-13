package per.goweii.android.rxhttp;

import io.reactivex.Observable;
import per.goweii.android.rxhttp.bean.RecommendPoetryBean;
import per.goweii.android.rxhttp.bean.SinglePoetryBean;
import retrofit2.http.GET;

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
}
