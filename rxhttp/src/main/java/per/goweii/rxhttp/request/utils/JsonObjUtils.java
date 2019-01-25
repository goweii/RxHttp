package per.goweii.rxhttp.request.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2018/10/8
 */
public class JsonObjUtils {

    private final JSONObject mJsonObject;

    private JsonObjUtils() {
        mJsonObject = new JSONObject();
    }

    public static JsonObjUtils create() {
        return new JsonObjUtils();
    }

    public JsonObjUtils add(String key, int value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonObjUtils add(String key, float value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonObjUtils add(String key, double value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonObjUtils add(String key, boolean value) {
        try {
            mJsonObject.put(key, value ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JsonObjUtils add(String key, String value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONObject get() {
        return mJsonObject;
    }

    public String toJson() {
        return mJsonObject.toString();
    }
}
