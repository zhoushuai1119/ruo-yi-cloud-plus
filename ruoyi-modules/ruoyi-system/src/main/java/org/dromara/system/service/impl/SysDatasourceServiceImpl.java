package org.dromara.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.creator.DataSourceProperty;
import com.baomidou.dynamic.datasource.creator.hikaricp.HikariDataSourceCreator;
import com.baomidou.dynamic.datasource.exception.CannotFindDataSourceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.dromara.common.core.constant.GlobalConstants.DEFAULT_TENANT_SOURCE;

/**
 * 多数据源配置Service业务层处理
 *
 * @author LionLi
 * @date 2023-11-13
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysDatasourceServiceImpl implements ISysDatasourceService, InitializingBean {

    private final SysDatasourceMapper baseMapper;

    private final DataSource dataSource;

    private final HikariDataSourceCreator hikariDataSourceCreator;

    /**
     * 多数据源启动初始化; 防止项目重启多数据源配置失效
     *
     * @author: zhou shuai
     * @date: 2023/11/14 11:13
     */
    @Override
    public void afterPropertiesSet() {
        log.info("========多数据源启动初始化==============");
        List<SysDatasource> dataSourceList = baseMapper.selectList();
        if (CollUtil.isNotEmpty(dataSourceList)) {
            dataSourceList.forEach(dataSource -> {
                SysDatasourceBo sysDatasourceBo = BeanUtil.copyProperties(dataSource, SysDatasourceBo.class);
                Set<String> dynamicDataSourceList = addDynamicDataSource(sysDatasourceBo);
                log.info("当前所有数据源为:{}", dynamicDataSourceList);
            });
        }
    }

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
        lqw.like(StringUtils.isNotBlank(bo.getName()), SysDatasource::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增多数据源配置
     */
    @Override
    public Boolean insertByBo(SysDatasourceBo bo) {
        SysDatasource add = MapstructUtils.convert(bo, SysDatasource.class);
        validEntityBeforeSave(add, false);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            Set<String> dynamicDataSourceList = addDynamicDataSource(bo);
            log.info("当前所有数据源为:{}", dynamicDataSourceList);
        }
        return flag;
    }

    /**
     * 修改多数据源配置
     */
    @Override
    public Boolean updateByBo(SysDatasourceBo bo) {
        SysDatasource update = MapstructUtils.convert(bo, SysDatasource.class);
        validEntityBeforeSave(update, true);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            removeDynamicDataSource(bo.getName());
            Set<String> dynamicDataSourceList = addDynamicDataSource(bo);
            log.info("当前所有数据源为:{}", dynamicDataSourceList);
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysDatasource entity, boolean isUpdate) {
        if (Objects.equals(DEFAULT_TENANT_SOURCE, entity.getName())) {
            throw new ServiceException("禁止添加名称为master数据源!!!");
        }
        boolean success = JdbcUtils.isConnectionOK(entity.getUrl(), entity.getUserName(), entity.getPassword());
        if (!success) {
            throw new IllegalArgumentException("数据源配置不正确，无法进行连接");
        }
        LambdaQueryWrapper<SysDatasource> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(entity.getName()), SysDatasource::getName, entity.getName());
        if (isUpdate) {
            lqw.ne(Objects.nonNull(entity.getId()), SysDatasource::getId, entity.getId());
        }
        SysDatasourceVo sysDatasourceVo = baseMapper.selectVoOne(lqw);
        if (Objects.nonNull(sysDatasourceVo)) {
            throw new ServiceException("数据库名称:" + entity.getName() + "已存在,不能重复配置!!!");
        }
    }

    /**
     * 批量删除多数据源配置
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            for (Long id : ids) {
                SysDatasource sysDatasource = baseMapper.selectById(id);
                // 删除数据源
                removeDynamicDataSource(sysDatasource.getName());
            }
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
        dataSourceProperty.setPoolName(bo.getName());
        dataSourceProperty.setType(HikariDataSource.class);
        dataSourceProperty.setUsername(bo.getUserName());
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        DataSource dataSource = hikariDataSourceCreator.createDataSource(dataSourceProperty);
        ds.addDataSource(bo.getName(), dataSource);
        return ds.getDataSources().keySet();
    }

    /**
     * 删除数据源
     *
     * @author: zhou shuai
     * @date: 2023/11/14 10:44
     * @param: datasourceName
     */
    private void removeDynamicDataSource(String datasourceName) {
        log.info("移除数据源:{}", datasourceName);
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource) dataSource;
        try {
            DataSource dynamicDataSource = ds.getDataSource(datasourceName);
            if (dynamicDataSource != null) {
                ds.removeDataSource(datasourceName);
            }
        } catch (CannotFindDataSourceException e) {
            log.warn("未查询到数据源:{}", datasourceName);
        }
    }

}
