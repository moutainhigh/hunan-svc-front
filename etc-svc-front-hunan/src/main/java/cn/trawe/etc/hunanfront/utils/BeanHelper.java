package cn.trawe.etc.hunanfront.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Guangxing
 */
public class BeanHelper {
    public BeanHelper() {
    }

    public static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    public static List<Field> getAllFieldList(Class clz) {
        List<Field> fields = new ArrayList<Field>();
        while (clz != null && !clz.getName().toLowerCase().equals("java.lang.object")) {// 当父类为null的时候说明到达了最上层的父类.
            fields.addAll(BeanHelper.getFieldList(clz));
            clz = clz.getSuperclass(); // 得到父类,然后赋给自己
        }
        return fields;
    }

    public static List<Field> getFieldList(Class clz) {
        List<Field> result = new ArrayList<Field>();
        if (clz == null) {
            return result;
        }

        Field[] fields = clz.getDeclaredFields();

        // 过滤掉静态字段,序列化字段等无用字段
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (field.getName().equals("")) {
                continue;
            }
            result.add(field);
        }

        return result;
    }

    public static Object getFieldValue(Field field, Object obj) {
        if (field != null && obj != null) {
            try {
                if (field.isAccessible()) {
                    return field.get(obj);
                } else {
                    field.setAccessible(true);
                    return field.get(obj);
                }
            } catch (IllegalAccessException var3) {
                throw new RuntimeException(var3);
            }
        } else {
            throw new IllegalArgumentException("参数为空");
        }
    }
}
