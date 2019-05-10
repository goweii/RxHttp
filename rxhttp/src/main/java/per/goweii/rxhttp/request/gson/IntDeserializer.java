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
public class IntDeserializer implements JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            return 0;
        }
        try {
            return json.getAsInt();
        } catch (Exception e){
        }
        try {
            double d = json.getAsDouble();
            return (int) d;
        } catch (Exception e){
        }
        try {
            String s = json.getAsString();
            return Integer.valueOf(s);
        } catch (Exception e){
        }
        return 0;
    }
}
