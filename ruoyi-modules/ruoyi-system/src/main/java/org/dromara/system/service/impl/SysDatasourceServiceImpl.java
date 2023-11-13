package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.hikaricp.HikariDataSourceCreator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.utils.JdbcUtils;
import org.dromara.system.domain.SysDatasource;
import org.dromara.system.domain.bo.SysDatasourceBo;
import org.dromara.system.domain.vo.SysDatasourceVo;
import org.dromara.system.mapper.SysDatasourceMapper;
import org.dromara.system.service.ISysDatasourceService;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 多数据源配置Service业务层处理
 *
 * @author LionLi
 * @date 2023-11-13
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysDatasourceServiceImpl implements ISysDatasourceService {

    private final SysDatasourceMapper baseMapper;

    private final DataSource dataSource;

    private final HikariDataSourceCreator hikariDataSourceCreator;

    /**
     * 查询多数据源配置
     */
    @Override
    public SysDatasourceVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 查询多数据源配置列表
     */
    @Override
    public TableDataInfo<SysDatasourceVo> queryPageList(SysDatasourceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysDatasource> lqw = buildQueryWrapper(bo);
        Page<SysDatasourceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询多数据源配置列表
     */
    @Override
    public List<SysDatasourceVo> queryList(SysDatasourceBo bo) {
        LambdaQueryWrapper<SysDatasource> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SysDatasource> buildQueryWrapper(SysDatasourceBo bo) {
        LambdaQueryWrapper<SysDatasource> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getTenantId()), SysDatasource::getTenantId, bo.getTenantId());
        return lqw;
    }

    /**
     * 新增多数据源配置
     */
    @Override
    public Boolean insertByBo(SysDatasourceBo bo) {
        SysDatasource add = MapstructUtils.convert(bo, SysDatasource.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            // 动态新增数据源
            Set<String> dataSourceList = addDynamicDataSource(bo);
            log.info("当前所有数据源为:{}", dataSourceList);
        }
        return flag;
    }

    /**
     * 修改多数据源配置
     */
    @Override
    public Boolean updateByBo(SysDatasourceBo bo) {
        SysDatasource update = MapstructUtils.convert(bo, SysDatasource.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysDatasource entity) {
        boolean success = JdbcUtils.isConnectionOK(entity.getUrl(), entity.getUserName(), entity.getPassword());
        if (!success) {
            throw new IllegalArgumentException("数据源配置不正确，无法进行连接");
        }
    }

    /**
     * 批量删除多数据源配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 动态新增数据源
     *
     * @author: zhou shuai
     * @date: 2023/11/14 0:23
     * @param: bo
     * @return: java.util.Set<java.lang.String>
     */
    private Set<String> addDynamicDataSource(SysDatasourceBo bo) {
        DataSourceProperty dataSourceProperty = new DataSourceProperty();
        BeanUtil.copyProperties(bo, dataSourceProperty);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = hikariDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(bo.getName(), dataSource);
        return ds.getDataSources().keySet();
    }

}
