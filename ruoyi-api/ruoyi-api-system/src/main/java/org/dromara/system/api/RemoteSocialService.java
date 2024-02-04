package org.dromara.system.api;

import org.dromara.system.api.domain.bo.RemoteSocialBo;
import org.dromara.system.api.domain.vo.RemoteSocialVo;

/**
 * 社会化关系服务
 *
 * @author shuai.zhou
 */
public interface RemoteSocialService {

    /**
     * 查询用户信息
     *
     * @author: zhou shuai
     * @date: 2023/11/17 20:08
     * @param: authId
     * @param: tenantId
     * @return: org.dromara.system.api.domain.vo.RemoteSocialVo
     */
    RemoteSocialVo selectByAuthId(String authId, String tenantId);

    /**
     * 保存社会化关系
     */
    void insertByBo(RemoteSocialBo bo);

    /**
     * 更新社会化关系
     */
    void updateByBo(RemoteSocialBo bo);

    /**
     * 删除社会化关系
     */
    Boolean deleteWithValidById(Long socialId);

}
