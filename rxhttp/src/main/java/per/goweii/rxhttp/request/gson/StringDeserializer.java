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
public class StringDeserializer implements JsonDeserializer<String> {
    @Nullable
    @Override
    public String deserialize(@NonNull JsonElement json, @NonNull Type typeOfT, @NonNull JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return "";
        }
        try {
            return json.getAsString();
        } catch (Exception ignore){
        }
        try {
            long l = json.getAsLong();
            return String.valueOf(l);
        } catch (Exception ignore){
        }
        try {
            double d = json.getAsDouble();
            return String.valueOf(d);
        } catch (Exception ignore){
        }
        return "";
    }
}
