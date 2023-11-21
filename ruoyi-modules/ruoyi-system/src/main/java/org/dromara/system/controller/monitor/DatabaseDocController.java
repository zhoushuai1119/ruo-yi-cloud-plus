package org.dromara.system.controller.monitor;

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
    @GetMapping("/exportHtml")
    public void exportHtml() {
        databaseDocService.doExportFile(EngineFileType.HTML);
    }

}
