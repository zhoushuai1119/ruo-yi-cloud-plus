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
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysClientBo;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.service.ISysClientService;
import org.dromara.system.service.ISysConfigService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 客户端管理
 *
 * @author Michelle.Chung
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/client")
public class SysClientController extends BaseController {

    private final ISysClientService sysClientService;

    private final ISysConfigService configService;

    /**
     * 查询客户端管理列表
     */
    @SaCheckPermission("system:client:list")
    @GetMapping("/list")
    public TableDataInfo<SysClientVo> list(SysClientBo bo, PageQuery pageQuery) {
        return sysClientService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出客户端管理列表
     */
    @SaCheckPermission("system:client:export")
    @Log(title = "客户端管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysClientBo bo, HttpServletResponse response) {
        List<SysClientVo> list = sysClientService.queryList(bo);
        String isWatermark = configService.selectConfigByKey("sys.export-download.watermark");
        if (Objects.equals("true", isWatermark)) {
            String waterMarkContent = LoginHelper.getUsername();
            ExcelUtil.exportWaterMarkExcel(list, "客户端管理", SysClientVo.class, waterMarkContent, response);
        } else {
            ExcelUtil.exportExcel(list, "客户端管理", SysClientVo.class, response);
        }
    }

    /**
     * 获取客户端管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:client:query")
    @GetMapping("/{id}")
    public R<SysClientVo> getInfo(@NotNull(message = "主键不能为空")
                                  @PathVariable Long id) {
        return R.ok(sysClientService.queryById(id));
    }

    /**
     * 新增客户端管理
     */
    @SaCheckPermission("system:client:add")
    @Log(title = "客户端管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysClientBo bo) {
        return toAjax(sysClientService.insertByBo(bo));
    }

    /**
     * 修改客户端管理
     */
    @SaCheckPermission("system:client:edit")
    @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysClientBo bo) {
        return toAjax(sysClientService.updateByBo(bo));
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:client:edit")
    @Log(title = "客户端管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysClientBo bo) {
        return toAjax(sysClientService.updateUserStatus(bo.getId(), bo.getStatus()));
    }

    /**
     * 删除客户端管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:client:remove")
    @Log(title = "客户端管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysClientService.deleteWithValidByIds(List.of(ids), true));
    }
}
