package org.dromara.system.dubbo;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.system.api.RemoteSocialService;
import org.dromara.system.api.domain.bo.RemoteSocialBo;
import org.dromara.system.api.domain.vo.RemoteSocialVo;
import org.dromara.system.domain.bo.SysSocialBo;
import org.dromara.system.domain.vo.SysSocialVo;
import org.dromara.system.service.ISysSocialService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社会化关系服务
 *
 * @author shuai.zhou
 */
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSocialServiceImpl implements RemoteSocialService {

    private final ISysSocialService sysSocialService;

    /**
     * 根据 authId 查询用户信息
     */
    @Override
    public List<RemoteSocialVo> selectByAuthId(String authId) {
        List<SysSocialVo> list = sysSocialService.selectByAuthId(authId);
        return MapstructUtils.convert(list, RemoteSocialVo.class);
    }

    @Override
    public List<RemoteSocialVo> queryList(RemoteSocialBo bo) {
        SysSocialBo params = MapstructUtils.convert(bo, SysSocialBo.class);
        List<SysSocialVo> list = sysSocialService.queryList(params);
        return MapstructUtils.convert(list, RemoteSocialVo.class);
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
