package org.dromara.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysDatasourceBo;
import org.dromara.system.domain.vo.SysDatasourceVo;
import org.dromara.system.service.ISysDatasourceService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 多数据源配置
 * 前端访问路由地址为:/system/datasourceConfig
 *
 * @author LionLi
 * @date 2023-11-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/datasource")
public class SysDatasourceController extends BaseController {

    private final ISysDatasourceService sysDatasourceService;

    /**
     * 查询多数据源配置列表
     */
    @SaCheckPermission("system:datasource:list")
    @GetMapping("/list")
    public TableDataInfo<SysDatasourceVo> list(SysDatasourceBo bo, PageQuery pageQuery) {
        return sysDatasourceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出多数据源配置列表
     */
    @SaCheckPermission("system:datasource:export")
    @Log(title = "多数据源配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysDatasourceBo bo, HttpServletResponse response) {
        List<SysDatasourceVo> list = sysDatasourceService.queryList(bo);
        ExcelUtil.exportExcel(list, "多数据源配置", SysDatasourceVo.class, response);
    }

    /**
     * 获取多数据源配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:datasource:query")
    @GetMapping("/{id}")
    public R<SysDatasourceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysDatasourceService.queryById(id));
    }

    /**
     * 新增多数据源配置
     */
    @SaCheckPermission("system:datasource:add")
    @Log(title = "多数据源配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysDatasourceBo bo) {
        return toAjax(sysDatasourceService.insertByBo(bo));
    }

    /**
     * 修改多数据源配置
     */
    @SaCheckPermission("system:datasource:edit")
    @Log(title = "多数据源配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysDatasourceBo bo) {
        return toAjax(sysDatasourceService.updateByBo(bo));
    }

    /**
     * 删除多数据源配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:datasource:remove")
    @Log(title = "多数据源配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysDatasourceService.deleteWithValidByIds(List.of(ids), true));
    }
}
