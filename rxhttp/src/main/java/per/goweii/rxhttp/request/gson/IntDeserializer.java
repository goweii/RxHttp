package per.goweii.rxhttp.request.gson;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/5/10
 */
public class IntDeserializer implements JsonDeserializer<Integer> {
    @Nullable
    @Override
    public Integer deserialize(@NonNull JsonElement json, @NonNull Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return 0;
        }
        try {
            return json.getAsInt();
        } catch (Exception ignore){
        }
        try {
            double d = json.getAsDouble();
            return (int) d;
        } catch (Exception ignore){
        }
        try {
            String s = json.getAsString();
            return Integer.valueOf(s);
        } catch (Exception ignore){
        }
        return 0;
    }
}
