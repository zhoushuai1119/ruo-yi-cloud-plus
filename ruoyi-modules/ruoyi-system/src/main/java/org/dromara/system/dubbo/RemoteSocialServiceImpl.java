package org.dromara.system.dubbo;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.system.api.RemoteSocialService;
import org.dromara.system.api.domain.bo.RemoteSocialBo;
import org.dromara.system.api.domain.vo.RemoteSocialVo;
import org.dromara.system.domain.bo.SysSocialBo;
import org.dromara.system.domain.vo.SysSocialVo;
import org.dromara.system.mapper.SysSocialMapper;
import org.dromara.system.service.ISysSocialService;
import org.springframework.stereotype.Service;

/**
 * 社会化关系服务
 *
 * @author Michelle.Chung
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSocialServiceImpl implements RemoteSocialService {

    private final ISysSocialService sysSocialService;
    private final SysSocialMapper sysSocialMapper;

    /**
     * 查询用户信息
     *
     * @author: zhou shuai
     * @date: 2023/11/17 20:08
     * @param: authId
     * @param: tenantId
     * @return: org.dromara.system.api.domain.vo.RemoteSocialVo
     */
    @Override
    public RemoteSocialVo selectByAuthId(String authId, String tenantId) {
        SysSocialVo socialVo = sysSocialService.selectByAuthId(authId, tenantId);
        return MapstructUtils.convert(socialVo, RemoteSocialVo.class);
    }

    /**
     * 保存社会化关系
     */
    @Override
    public void insertByBo(RemoteSocialBo bo) {
        sysSocialService.insertByBo(MapstructUtils.convert(bo, SysSocialBo.class));
    }

    /**
     * 更新社会化关系
     */
    @Override
    public void updateByBo(RemoteSocialBo bo) {
        sysSocialService.updateByBo(MapstructUtils.convert(bo, SysSocialBo.class));
    }

    /**
     * 删除社会化关系
     */
    @Override
    public Boolean deleteWithValidById(Long socialId) {
        return sysSocialService.deleteWithValidById(socialId);
    }

}
