package per.goweii.rxhttp.base;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * 描述：网络请求的实体类基类
 *
 * @author Cuizhen
 * @date 2018/9/9
 */
public class BaseBean implements Serializable {

    public String toJson() {
        return new Gson().toJson(this);
    }
}