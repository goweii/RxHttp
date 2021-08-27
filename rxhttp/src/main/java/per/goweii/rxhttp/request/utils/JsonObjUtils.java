package per.goweii.rxhttp.request.utils;

import android.support.annotation.NonNull;

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

    @NonNull
    public static JsonObjUtils create() {
        return new JsonObjUtils();
    }

    @NonNull
    public JsonObjUtils add(@NonNull String key, int value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    public JsonObjUtils add(@NonNull String key, float value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    public JsonObjUtils add(@NonNull String key, double value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    public JsonObjUtils add(@NonNull String key, boolean value) {
        try {
            mJsonObject.put(key, value ? 1 : 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    public JsonObjUtils add(@NonNull String key, String value) {
        try {
            mJsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    public JSONObject get() {
        return mJsonObject;
    }

    @NonNull
    public String toJson() {
        return mJsonObject.toString();
    }
}
