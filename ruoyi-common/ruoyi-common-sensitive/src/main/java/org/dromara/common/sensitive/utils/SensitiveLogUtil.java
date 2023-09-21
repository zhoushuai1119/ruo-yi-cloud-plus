package org.dromara.common.sensitive.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.reflect.ReflectUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.sensitive.annotation.Sensitive;
import org.dromara.common.sensitive.core.SensitiveService;
import org.dromara.common.sensitive.core.SensitiveStrategy;
import org.springframework.beans.BeansException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @description: 脱敏日志打印
 * @author: zhou shuai
 * @date: 2023/9/21 18:42
 * @version: v1
 */
@Slf4j
public class SensitiveLogUtil {

    public static <T> String toJsonStr(T t) {
        try {
            SensitiveService sensitiveService = SpringUtils.getBean(SensitiveService.class);
            if (ObjectUtil.isNotNull(sensitiveService) && sensitiveService.isSensitive()) {
                return getJsonStr(t);
            } else {
                return JsonUtils.toJsonString(t);
            }
        } catch (BeansException e) {
            log.warn("脱敏实现不存在, 采用默认处理 => {}", e.getMessage());
            return JsonUtils.toJsonString(t);
        }
    }

    private static <T> String getJsonStr(T t) {
        Field[] fields = ReflectUtils.getFields(t.getClass());
        if (ArrayUtil.isNotEmpty(fields)) {
            for (Field field : fields) {
                // 剔除实体类中serialVersionUID字段
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Sensitive sensitive = field.getAnnotation(Sensitive.class);
                if (sensitive != null) {
                    SensitiveStrategy strategy = sensitive.strategy();
                    Object fieldValue = ReflectUtils.getFieldValue(t, field);
                    if (Objects.nonNull(fieldValue)) {
                        String sensitiveFieldValue = strategy.desensitizer().apply(String.valueOf(fieldValue));
                        ReflectUtils.setFieldValue(t, field, sensitiveFieldValue);
                    }
                }
            }
        }
        return JsonUtils.toJsonString(t);
    }

}
