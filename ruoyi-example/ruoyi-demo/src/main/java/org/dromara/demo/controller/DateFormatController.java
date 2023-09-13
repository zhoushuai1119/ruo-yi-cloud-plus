package org.dromara.demo.controller;

import org.dromara.common.core.domain.R;
import org.dromara.demo.domain.bo.DateFormatBo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/9/13 23:18
 * @version: v1
 */
@RestController
@RequestMapping("/date")
public class DateFormatController {

    @GetMapping("/format")
    public R<Object> dateFormat(DateFormatBo dateFormatBo) {
        dateFormatBo.setTest2(new Date());
        dateFormatBo.setLocalDate(LocalDate.now());
        dateFormatBo.setLocalDateTime(LocalDateTime.now());
        return R.ok(dateFormatBo);
    }

}
