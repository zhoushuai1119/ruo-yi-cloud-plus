package com.alibaba.cloud.sentinel.parser;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import static org.dromara.common.core.constant.SentinelConstants.SENTINEL_ORIGIN_HEADER;


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
        String origin = request.getHeader(SENTINEL_ORIGIN_HEADER);
        if (StrUtil.isBlank(origin)) {
            origin = StrUtil.EMPTY;
        }
        if (log.isDebugEnabled()) {
            log.debug("sentinel authority rule request header origin is {}", origin);
        }
        return origin;
    }

}
