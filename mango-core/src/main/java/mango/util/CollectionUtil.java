package mango.util;

import java.util.Collection;
import java.util.Map;

/**
 * ${DESCRIPTION}
 * 集合工具类，判断非空
 * @author Ricky Fung
 */
public class CollectionUtil {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
}
