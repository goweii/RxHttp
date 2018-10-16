package per.goweii.rxhttp.request.utils;

import java.util.Collection;
import java.util.Map;

/**
 * 判断一个对象是否非空
 *
 * @author Cuizhen
 * @date 2018/10/15
 */
public class NonNullUtils {

    public static boolean check(Map... maps) {
        if (maps == null || maps.length == 0) {
            return false;
        }
        for (Map map : maps) {
            if (check(map)) {
                return true;
            }
        }
        return false;
    }

    public static boolean check(Collection... collections) {
        if (collections == null || collections.length == 0) {
            return false;
        }
        for (Collection collection : collections) {
            if (check(collection)) {
                return true;
            }
        }
        return false;
    }

    public static boolean check(Object... objects) {
        if (objects != null && objects.length > 0) {
            for (Object o : objects) {
                if (check(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean check(Map map) {
        return map != null && !map.isEmpty();
    }

    public static boolean check(Collection collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean check(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Object[]){
            Object[] objects = (Object[]) o;
            return check(objects);
        }if (o instanceof Collection) {
            Collection collection = (Collection) o;
            return check(collection);
        } else if (o instanceof Map){
            Map map = (Map) o;
            return check(map);
        }else {
            return true;
        }
    }
}
