package per.goweii.rxhttp.request.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author Cuizhen
 * @date 2019/5/10
 */
public class ListDeserializer implements JsonDeserializer<List<?>> {

    @Override
    public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonArray()) {
            return null;
        }
        List<Object> list = new ArrayList<>();
        JsonArray array = json.getAsJsonArray();
        Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            Object item = context.deserialize(element, itemType);
            list.add(item);
        }
        return list;
    }

}
