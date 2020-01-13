package cn.trawe.etc.hunanfront.utils;

import cn.trawe.etc.hunanfront.response.Precheck;
import cn.trawe.utils.reflect.BeanHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SignUtil {

    @Value("${alipay1.private_key}")
    private String privateKey;

    @Value("${alipay1.alipay_public_key}")
    private String alipayPublicKey;

    public <T> String sign(T object, String charset) {
        try {
            return sign(object, privateKey, charset);
        } catch (AlipayApiException e) {
            log.error("验签失败", e);
            return null;
        }
    }

    public <T> Boolean verify(T object, String charset) {
        try {
            return verify(object, alipayPublicKey, charset);
        } catch (AlipayApiException e) {
            log.error("签名失败", e);
            return false;
        }
    }

    public static <T> String sign(T object, String privateKey, String charset) throws AlipayApiException {
        if (object == null) {
            return null;
        }
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
        return AlipaySignature.rsa256Sign(JSON.toJSONString(object, serializeConfig), privateKey, charset);
    }

    public static <T> Boolean verify(T object, String publicKey, String charset) throws AlipayApiException {
        if (object == null) {
            return false;
        }
        List<Field> fields = getAllFieldList(object.getClass());
        Map<String, String> params = new HashMap<>();
        for (Field field : fields) {
            String fieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, field.getName());
            Object fieldValue = BeanHelper.getFieldValue(field, object);
            if (fieldValue != null) {
                if (fieldValue instanceof String || field.getType().isEnum() || field.getType().isPrimitive() || isWrapClass(field.getType())) {
                    params.put(fieldName, String.valueOf(fieldValue));
                } else {
                    SerializeConfig serializeConfig = new SerializeConfig();
                    serializeConfig.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
                    params.put(fieldName, JSON.toJSONString(fieldValue));
                }
            }
        }
        return AlipaySignature.rsaCheckV1(params, publicKey, charset, "RSA2");
    }

    public static void main(String[] args) {
        Precheck precheck = new Precheck();
        List<Field> fields = getAllFieldList(precheck.getClass());
        System.out.println(fields);
    }

    private static List<Field> getAllFieldList(Class tempClass) {
        List<Field> fields = new ArrayList<>();
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {// 当父类为null的时候说明到达了最上层的父类.
            fields.addAll(new BeanHelper().getFieldList(tempClass));
            tempClass = tempClass.getSuperclass(); // 得到父类,然后赋给自己
        }
        return fields;
    }

    private static boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
