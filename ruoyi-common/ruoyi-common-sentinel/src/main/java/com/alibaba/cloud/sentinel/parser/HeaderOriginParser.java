package com.alibaba.cloud.sentinel.parser;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * @description: sentinel 授权规则
 * @author: shuai.zhou
 * @date: 2022/10/21 13:41
 */
@Slf4j
public class HeaderOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest request) {
        // 获取请求头
        String origin = request.getHeader("origin");
        if (StringUtils.isBlank(origin)) {
            origin = "blank";
        }
        log.info("sentinel request header origin is {}", origin);
        return origin;
    }

}
