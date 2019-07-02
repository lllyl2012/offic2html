package top.lllyl2012.html.utils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 逻辑判断工具
 */
public class LogicUtil {

    /**
     *  String,List,Map,Object[],int[],long[]
     */
    public static boolean isEmpty(Object o) {
        if (null == o) {
            return true;
        }
        if (o instanceof List) {
            List list = (List) o;
            return list.size() <= 0;
        } else if (o instanceof Object[]) {
            Object[] objs = (Object[]) o;
            return objs.length <= 0;
        } else if (o instanceof String) {
            return "".equals(o.toString().trim()) || "null".equals(o.toString().trim());
        } else if (o instanceof Map) {
            return ((Map) o).size() == 0;
        } else if (o instanceof int[]) {
            return ((int[]) o).length == 0;
        } else if (o instanceof long[]) {
            return ((long[]) o).length == 0;
        }
        return false;
    }

    /**
     * 对象是否不为空
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

}