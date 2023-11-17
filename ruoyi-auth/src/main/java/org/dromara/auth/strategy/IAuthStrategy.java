package org.dromara.auth.strategy;

import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.common.core.enums.LoginType;
import org.dromara.system.api.domain.vo.RemoteClientVo;

/**
 * @description: 登录认证策略顶级接口
 * @author: zhou shuai
 * @date: 2023/11/17 10:20
 * @version: v1
 */
public interface IAuthStrategy {

    /**
     * 登录
     *
     * @author: zhou shuai
     * @date: 2023/11/17 10:22
     * @param: body
     * @param: client
     * @return: org.dromara.auth.domain.vo.LoginVo
     */
    LoginVo login(String body, RemoteClientVo client);

    /**
     * 登录类型
     *
     * @author: zhou shuai
     * @date: 2023/11/17 10:47
     * @return: org.dromara.common.core.enums.LoginType
     */
    LoginType loginType();

}
