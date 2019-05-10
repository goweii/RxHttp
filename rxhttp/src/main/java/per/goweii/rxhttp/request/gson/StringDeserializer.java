package per.goweii.rxhttp.request.gson;

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
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return "";
        }
        try {
            return json.getAsString();
        } catch (Exception e){
        }
        try {
            long l = json.getAsLong();
            return String.valueOf(l);
        } catch (Exception e){
        }
        try {
            double d = json.getAsDouble();
            return String.valueOf(d);
        } catch (Exception e){
        }
        return "";
    }
}
