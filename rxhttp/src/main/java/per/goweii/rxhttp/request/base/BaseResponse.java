package per.goweii.rxhttp.request.base;

/**
 * 描述：网络接口返回json格式对应的实体类
 *
 * @author Cuizhen
 * @date 2018/6/19
 */
public interface BaseResponse<E> {

    int getCode();

    void setCode(int code);

    E getData();

    void setData(E data);

    String getMsg();

    void setMsg(String msg);
}
