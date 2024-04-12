package org.dromara.common.redis.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;

import java.io.IOException;
import java.io.Serial;

/**
 * Long值太大转成字符串，防止前端显示精度丢失
 *
 * @author shuai.zhou
 */
@JacksonStdImpl
public class BigNumberSerializer extends NumberSerializer {

    @Serial
    private static final long serialVersionUID = -5753687097218162819L;

    /**
     * 根据 JS Number.MAX_SAFE_INTEGER 与 Number.MIN_SAFE_INTEGER 得来
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    /**
     * 提供实例
     */
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer(Number.class);

    public BigNumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    @Override
    public void serialize(Number value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // 超出范围 序列化位字符串
        if (value.longValue() > MIN_SAFE_INTEGER && value.longValue() < MAX_SAFE_INTEGER) {
            super.serialize(value, gen, provider);
        } else {
            gen.writeString(value.toString());
        }
    }
}
