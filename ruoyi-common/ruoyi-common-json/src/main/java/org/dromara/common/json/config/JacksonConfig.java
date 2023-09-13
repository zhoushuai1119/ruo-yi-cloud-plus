package org.dromara.common.json.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.json.handler.BigNumberSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * jackson 配置
 *
 * @author Lion Li
 */
@Slf4j
@AutoConfiguration(before = JacksonAutoConfiguration.class)
public class JacksonConfig {

    private static final SimpleDateFormat DATE_FORMAT_TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MY_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MY_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> {
            // 全局配置序列化返回 JSON 处理
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
            javaTimeModule.addSerializer(BigDecimal.class, ToStringSerializer.instance);
            // 将前端传入的字符串转为 LocalDateTime
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(MY_DATE_TIME));
            // 将后端 LocalDateTime 转换为 MY_DATE_TIME 格式返回前端
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(MY_DATE_TIME));
            // 将前端传入的字符串转为 LocalDate
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(MY_DATE));
            //将后端 LocalDate 转换为 MY_DATE 格式返回前端
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(MY_DATE));
            builder.modules(javaTimeModule);
            builder.timeZone(TimeZone.getDefault());
            // 设置Date日期格式,不影响LocalDateTime和LocalDate
            builder.dateFormat(DATE_FORMAT_TIME);
            // 空值不序列化
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            log.info("初始化 jackson 配置");
        };
    }

}
