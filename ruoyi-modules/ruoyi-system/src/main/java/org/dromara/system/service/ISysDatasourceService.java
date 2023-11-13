package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.SysDatasourceBo;
import org.dromara.system.domain.vo.SysDatasourceVo;

import java.util.Collection;
import java.util.List;

/**
 * 多数据源配置Service接口
 *
 * @author LionLi
 * @date 2023-11-13
 */
public interface ISysDatasourceService {

    /**
     * 查询多数据源配置
     */
    SysDatasourceVo queryById(Long id);

    /**
     * 查询多数据源配置列表
     */
    TableDataInfo<SysDatasourceVo> queryPageList(SysDatasourceBo bo, PageQuery pageQuery);

    /**
     * 查询多数据源配置列表
     */
    List<SysDatasourceVo> queryList(SysDatasourceBo bo);

    /**
     * 新增多数据源配置
     */
    Boolean insertByBo(SysDatasourceBo bo);

    /**
     * 修改多数据源配置
     */
    Boolean updateByBo(SysDatasourceBo bo);

    /**
     * 校验并批量删除多数据源配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
