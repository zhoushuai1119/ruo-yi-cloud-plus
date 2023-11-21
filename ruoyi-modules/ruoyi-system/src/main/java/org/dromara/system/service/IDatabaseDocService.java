package org.dromara.system.service;

import cn.smallbun.screw.core.engine.EngineFileType;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/11/21 16:18
 * @version: v1
 */
public interface IDatabaseDocService {

    /**
     * 导出数据库文档
     *
     * @author: zhou shuai
     * @date: 2023/11/21 16:20
     * @param: fileOutputType
     */
    void doExportFile(EngineFileType fileOutputType);

}
