package cn.trawe.etc.hunanfront.utils;

import cn.trawe.utils.StrUtils;
import cn.trawe.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Jiang Guangxing
 */
@Slf4j
public class ValidUtils {
    public static String validateBean(Object obj) {
        if (null == obj)
            return "bizContent不能为空";
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Object>> validate = validator.validate(obj);
        if (ValidateUtil.isEmpty(validate) || validate.isEmpty())
            return "";

        StringBuilder error = new StringBuilder();

        for (ConstraintViolation<Object> cv : validate) {
            error.append(cv.getMessage()).append(",");
        }
        String errStr = error.toString();
        errStr = StrUtils.left(errStr, errStr.length() - 1);
        return errStr;
    }

    private ValidUtils() {
    }
}
