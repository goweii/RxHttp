package per.goweii.rxhttp.request.base;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;

import per.goweii.rxhttp.request.utils.JsonFormatUtils;

/**
 * 描述：网络请求的实体类基类
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class BaseBean implements Serializable {

    @NonNull
    public String toJson() {
        return new Gson().toJson(this);
    }

    @NonNull
    public String toFormatJson() {
        return JsonFormatUtils.format(toJson());
    }
}