package org.dromara.demo.domain.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/9/13 23:20
 * @version: v1
 */
@Data
public class DateFormatBo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2270397877174779672L;

    private Date test1;
    private Date test2;
    private LocalDate localDate;
    private LocalDateTime localDateTime;

}
