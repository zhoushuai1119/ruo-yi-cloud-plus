package org.dromara.system.domain.convert;

import io.github.linpeilie.BaseMapper;
import org.dromara.system.api.domain.vo.RemoteTenantDataSourceVo;
import org.dromara.system.domain.vo.SysDatasourceVo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * 租户数据源转换器
 * @author zhujie
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SysTenantDataSourceVoConvert extends BaseMapper<SysDatasourceVo, RemoteTenantDataSourceVo> {

}
