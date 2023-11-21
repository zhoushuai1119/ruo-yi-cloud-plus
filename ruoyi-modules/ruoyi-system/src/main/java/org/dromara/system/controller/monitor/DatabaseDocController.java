package org.dromara.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.smallbun.screw.core.engine.EngineFileType;
import lombok.RequiredArgsConstructor;
import org.dromara.system.service.IDatabaseDocService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据库文档
 *
 * @author shuai.zhou
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/database/doc")
public class DatabaseDocController {

    private final IDatabaseDocService databaseDocService;

    /**
     * 导出 html 格式的数据文档
     *
     * @author: zhou shuai
     * @date: 2023/11/21 16:16
     */
    @SaCheckPermission("monitor:dbdoc:list")
    @GetMapping("/exportHtml")
    public void exportHtml() {
        databaseDocService.doExportFile(EngineFileType.HTML);
    }

    /**
     * 导出 markdown 格式的数据文档
     *
     * @author: zhou shuai
     * @date: 2023/11/21 16:16
     */
    @GetMapping("/exportMarkdown")
    public void exportMarkdown() {
        databaseDocService.doExportFile(EngineFileType.MD);
    }

    /**
     * 导出 word 格式的数据文档
     *
     * @author: zhou shuai
     * @date: 2023/11/21 16:16
     */
    @GetMapping("/exportWord")
    public void exportWord() {
        databaseDocService.doExportFile(EngineFileType.WORD);
    }

}
