package org.dromara.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.redis.utils.RedissonUtil;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.SysLogininforBo;
import org.dromara.system.domain.vo.SysLogininforVo;
import org.dromara.system.service.ISysConfigService;
import org.dromara.system.service.ISysLogininforService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Objects;

/**
 * 系统访问记录
 *
 * @author shuai.zhou
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    private final ISysConfigService configService;

    /**
     * 获取系统访问记录列表
     */
    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininforVo> list(SysLogininforBo logininfor, PageQuery pageQuery) {
        return logininforService.selectPageLogininforList(logininfor, pageQuery);
    }

    /**
     * 导出系统访问记录列表
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(SysLogininforBo logininfor, HttpServletResponse response) {
        List<SysLogininforVo> list = logininforService.selectLogininforList(logininfor);
        String isWatermark = configService.selectConfigByKey("sys.export-download.watermark");
        if (Objects.equals("true", isWatermark)) {
            String waterMarkContent = LoginHelper.getUsername();
            ExcelUtil.exportWaterMarkExcel(list, "登录日志", SysLogininforVo.class, waterMarkContent, response);
        } else {
            ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo.class, response);
        }
    }

    /**
     * 批量删除登录日志
     *
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        return toAjax(logininforService.deleteLogininforByIds(infoIds));
    }

    /**
     * 清理系统访问记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R<Void> unlock(@PathVariable("userName") String userName) {
        String loginName = GlobalConstants.PWD_ERR_CNT_KEY + userName;
        if (RedissonUtil.hasKey(loginName)) {
            RedissonUtil.deleteObject(loginName);
        }
        return R.ok();
    }

}
