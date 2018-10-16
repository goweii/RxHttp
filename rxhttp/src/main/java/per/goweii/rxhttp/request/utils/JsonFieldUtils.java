package per.goweii.rxhttp.request.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/8
 */
public class JsonFieldUtils {

    private final JSONObject mJsonObject;

    private JsonFieldUtils() {
        mJsonObject = new JSONObject();
    }

    public static JsonFieldUtils newInstance() {
        return new JsonFieldUtils();
    }

    public JsonFieldUtils add(String key, int value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonFieldUtils add(String key, float value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonFieldUtils add(String key, double value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonFieldUtils add(String key, boolean value) {
        try {
            mJsonObject.put(key, value ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonFieldUtils add(String key, String value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String toJson() {
        return mJsonObject.toString();
    }
}
